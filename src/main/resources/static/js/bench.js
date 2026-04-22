async function runBenchmark() {
    // New simple UI: size (n), min and max values, repetitions
    const n = parseInt(document.getElementById('sizeN').value) || 100;
    const minVal = parseInt(document.getElementById('minVal').value);
    const maxVal = parseInt(document.getElementById('maxVal').value);
    const reps = parseInt(document.getElementById('reps').value) || 3;

    const mode = (document.getElementById('mode') && document.getElementById('mode').value) ? document.getElementById('mode').value : 'random';
    // validate min/max for random mode
    if (mode === 'random' && !isNaN(minVal) && !isNaN(maxVal) && minVal > maxVal) {
        alert('El valor mínimo no puede ser mayor que el máximo.');
        return;
    }
    let payload = null;
    if (mode === 'manual') {
        const raw = document.getElementById('manualArray').value || '';
        const arr = raw.split(/[^0-9-]+/).map(s => parseInt(s)).filter(n => !isNaN(n));
        if (!arr.length) {
            alert('Modo manual: por favor provee un arreglo válido en "Arreglo manual".');
            return;
        }
        payload = { mode: 'manual', startN: arr.length, endN: arr.length, factor: 1, repetitions: reps, array: arr };
    } else {
        payload = { mode: 'random', startN: n, endN: n, factor: 1, repetitions: reps };
        if (!isNaN(minVal)) payload.min = minVal;
        if (!isNaN(maxVal)) payload.max = maxVal;
    }

    document.getElementById('resultInfo').innerText = 'Ejecutando benchmark... esto puede tardar.';

    try {
        const res = await fetch('/api/treesort/benchmark', {
            method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload)
        });
        // read response as text first (server may return HTML on error)
        const text = await res.text();
        let data;
        try {
            data = JSON.parse(text);
        } catch (e) {
            document.getElementById('resultInfo').innerText = 'Error: respuesta del servidor no es JSON válido.\n' + text.substring(0,1000);
            return;
        }

        // parse resultJson / resultadoJson safely (aceptamos nombres en inglés y en español)
        let result;
        try {
            const raw = data.resultJson || data.result || data.resultadoJson || data.resultado || data;
            if (typeof raw === 'string') result = JSON.parse(raw);
            else result = raw;
        } catch (e) {
            const snippet = (data.resultJson || data.resultadoJson || JSON.stringify(data)).toString().substring(0,1000);
            document.getElementById('resultInfo').innerText = 'Error parseando resultJson: ' + e.message + '\nRespuesta servidor: ' + snippet;
            return;
        }
        const points = result.points || [];
        console.log('runBenchmark: received points', points);

        const labels = points.map(p => p.n);
        // keep ms as float (not rounded) so small times aren't zeroed
        const means = points.map(p => (p.mean / 1e6)); // convert ns to ms (float)
        const stds = points.map(p => (p.std / 1e6));

        renderChart(labels, means, stds);

        // mostrar id y fecha (acepta createdAt o creadoEn)
        const created = data.createdAt || data.creadoEn || data.created || null;
        document.getElementById('resultInfo').innerText = JSON.stringify({id: data.id, creadoEn: created}, null, 2);
        // refresh saved runs list
        await loadSavedRuns();
        await loadAggregated();
    } catch (e) {
        document.getElementById('resultInfo').innerText = 'Error: ' + e.message;
    }
}

let chart = null;
function renderChart(labels, means, stds) {
    const ctx = document.getElementById('chart');
    if (chart) chart.destroy();
    if (!labels || labels.length === 0) {
        // no data: clear canvas and indicate message
        const info = document.getElementById('resultInfo');
        info.innerText = 'No hay datos para mostrar en la gráfica.';
        // ensure canvas is cleared
        if (ctx && ctx.getContext) {
            const c = ctx.getContext('2d'); c.clearRect(0,0,ctx.width || ctx.clientWidth, ctx.height || ctx.clientHeight);
        }
        return;
    }
    // normalize and sort points by n to ensure monotonic theoretical curves
    const pts = labels.map((x,i) => ({ n: Number(x), mean: Number(means[i] || 0), std: Number(stds[i] || 0) }));
    pts.sort((a,b) => a.n - b.n);
    const ns = pts.map(p => p.n);
    const meansMs = pts.map(p => p.mean);
    const stdsSorted = pts.map(p => p.std);

    function fitC(farr) {
        let num = 0, den = 0;
        for (let i = 0; i < ns.length; i++) {
            const f = farr(ns[i]); num += f * meansMs[i]; den += f * f;
        }
        return den === 0 ? 0 : num / den;
    }

    const cNLog = fitC(n => n * Math.log(n));
    const cN2 = fitC(n => n * n);

    // theory arrays are in same units as meansMs (ms), so do not divide by 1e6 here
    const theoryNLog = ns.map(n => (cNLog * n * Math.log(n)));
    const theoryN2 = ns.map(n => (cN2 * n * n));

    // compute R^2 for nlog fit
    function r2(pred) {
        const meanY = meansMs.reduce((a,b)=>a+b,0)/meansMs.length;
        let ssRes=0, ssTot=0;
        for (let i=0;i<meansMs.length;i++) { ssRes += Math.pow(meansMs[i]-pred[i],2); ssTot += Math.pow(meansMs[i]-meanY,2); }
        return 1 - (ssRes/ssTot || 1);
    }

    const predNLog = ns.map(n => (cNLog * n * Math.log(n)));
    const predN2 = ns.map(n => (cN2 * n * n));
    const r2NLog = r2(predNLog);
    const r2N2 = r2(predN2);

    // convert to Chart.js {x,y} points so x axis is numeric and curves preserve shape
    const measuredPoints = ns.map((n,i) => ({ x: n, y: meansMs[i] }));
    // plot pure theoretical functions (no fitting to measured results) using sorted ns
    const theoryPointsNLog = ns.map((nn) => ({ x: nn, y: nn * Math.log(nn) }));
    const theoryPointsN2 = ns.map((nn) => ({ x: nn, y: nn * nn }));

    // compute normalized overlays so theoretical shapes are visible on the same scale as measured data
    const maxMeasured = Math.max(...meansMs, 1);
    const maxTheoryNLog = Math.max(...theoryPointsNLog.map(p => p.y), 1);
    const maxTheoryN2 = Math.max(...theoryPointsN2.map(p => p.y), 1);
    const normScaleNLog = maxMeasured / maxTheoryNLog;
    const normScaleN2 = maxMeasured / maxTheoryN2;
    const normTheoryNLog = theoryPointsNLog.map(p => ({ x: p.x, y: p.y * normScaleNLog }));
    const normTheoryN2 = theoryPointsN2.map(p => ({ x: p.x, y: p.y * normScaleN2 }));

    chart = new Chart(ctx, {
        type: 'line', data: {
            datasets: [
                { label: 'Tiempo medio (ms)', data: measuredPoints, borderColor: '#38bdf8', backgroundColor: 'rgba(56,189,248,0.1)', tension:0.2, showLine:true, pointRadius:4 },
                { label: 'Teórico n·log(n) (escala propia)', data: theoryPointsNLog, borderColor: '#f59e0b', borderDash:[5,5], fill:false, pointRadius:0, yAxisID: 'yTheory' },
                { label: 'Teórico n^2 (escala propia)', data: theoryPointsN2, borderColor: '#ef4444', borderDash:[5,5], fill:false, pointRadius:0, yAxisID: 'yTheory' },
                { label: 'n·log(n) (normalizado)', data: normTheoryNLog, borderColor: '#f59e0b', borderDash:[2,2], borderWidth:1, backgroundColor:'rgba(245,158,11,0.08)', fill:false, pointRadius:0, yAxisID: 'y' },
                { label: 'n^2 (normalizado)', data: normTheoryN2, borderColor: '#ef4444', borderDash:[2,2], borderWidth:1, backgroundColor:'rgba(239,68,68,0.06)', fill:false, pointRadius:0, yAxisID: 'y' }
            ]
        }, options: { scales: { x: { type: 'linear', title: { display: true, text: 'n' } }, y: { beginAtZero: true, title: { display: true, text: 'Tiempo (ms)' } }, yTheory: { position: 'right', title: { display: true, text: 'Función teórica (unidad arbitraria)' }, grid: { drawOnChartArea: false } } }, plugins: { legend: { position: 'top' }, tooltip: { callbacks: { label: function(context) { const y = context.parsed.y; if (context.dataset.yAxisID === 'yTheory') return context.dataset.label + ': ' + y.toFixed(3); return context.dataset.label + ': ' + y.toFixed(3) + ' ms'; } } } } }
    });

    // show R2 values in analysis panel (but keep resultInfo for messages)
    const analysisEl = document.getElementById('analysisPanel');
    if (analysisEl) {
        let text = '';
        text += `Puntos mostrados: ${labels.length}\n`;
        text += `R^2 n·log(n): ${r2NLog.toFixed(4)} | R^2 n^2: ${r2N2.toFixed(4)}\n`;
        text += `Para análisis más detallado pulse '🧠 Analizar (heurístico cliente)'.\n`;
        analysisEl.innerText = text;
    }

    // render table of points (use sorted arrays)
    renderPointsTable(ns, meansMs, stdsSorted);

    // if user requested to scale theoretical functions, apply scaled overlays
    const scaleCb = document.getElementById('scaleTheories');
    if (scaleCb && scaleCb.checked) {
        const fit = computeFitCoefficients(ns, meansMs);
        applyScaledTheoreticalDatasets(fit.cNLog, fit.cN2);
        // append fit info
        if (analysisEl) analysisEl.innerText += `Ajuste (mínimos cuadrados): c_nlog = ${fit.cNLog.toExponential(3)}, c_n2 = ${fit.cN2.toExponential(3)}\n`;
    }
}

function renderPointsTable(labels, means, stds) {
    const wrapper = document.getElementById('pointsTableWrapper');
    if (!wrapper) return;
    if (!labels || labels.length === 0) { wrapper.innerHTML = '<div>No hay puntos para mostrar.</div>'; return; }
    let html = '<table style="width:100%; border-collapse:collapse; color:#e5e7eb;">';
    html += '<thead><tr style="text-align:left;"><th style="padding:6px">n</th><th style="padding:6px">mean (ms)</th><th style="padding:6px">std (ms)</th></tr></thead>';
    html += '<tbody>';
    for (let i=0;i<labels.length;i++) {
        html += `<tr><td style="padding:6px">${labels[i]}</td><td style="padding:6px">${means[i].toFixed(6)}</td><td style="padding:6px">${(stds[i]||0).toFixed(6)}</td></tr>`;
    }
    html += '</tbody></table>';
    wrapper.innerHTML = html;
}

function computeFitCoefficients(ns, meansMs) {
    // returns cNLog and cN2 fitted to meansMs (means in ms units)
    let sumF2 = 0, sumFM = 0;
    for (let i=0;i<ns.length;i++) { const f = ns[i] * Math.log(ns[i]); sumF2 += f*f; sumFM += f * meansMs[i]; }
    const cNLog = sumF2 === 0 ? 0 : sumFM / sumF2;

    let sumF22 = 0, sumFM2 = 0;
    for (let i=0;i<ns.length;i++) { const f = ns[i] * ns[i]; sumF22 += f*f; sumFM2 += f * meansMs[i]; }
    const cN2 = sumF22 === 0 ? 0 : sumFM2 / sumF22;
    return { cNLog, cN2 };
}

function applyScaledTheoreticalDatasets(cNLog, cN2) {
    if (!chart) return;
    // remove previous scaled datasets if any (ids: scaled-nlog, scaled-n2)
    chart.data.datasets = chart.data.datasets.filter(ds => !(ds._id === 'scaled-nlog' || ds._id === 'scaled-n2'));
    const labels = chart.data.datasets[0].data.map(p => Number(p.x));
    const scaledNLog = labels.map(n => ({ x: n, y: cNLog * n * Math.log(n) }));
    const scaledN2 = labels.map(n => ({ x: n, y: cN2 * n * n }));
    chart.data.datasets.push({ label: 'Teórico escalado c·n·log(n)', data: scaledNLog, borderColor: '#f59e0b', borderDash:[3,3], fill:false, pointRadius:0, _id: 'scaled-nlog', yAxisID: 'y' });
    chart.data.datasets.push({ label: 'Teórico escalado c·n^2', data: scaledN2, borderColor: '#ef4444', borderDash:[3,3], fill:false, pointRadius:0, _id: 'scaled-n2', yAxisID: 'y' });
    chart.update();
}

function exportDisplayedCSV() {
    if (!chart) { alert('No hay datos para exportar'); return; }
    const labels = chart.data.datasets[0].data.map(p => Number(p.x));
    const means = chart.data.datasets[0].data.map(p => Number(p.y));
    const rows = [['n','mean_ms']];
    for (let i=0;i<labels.length;i++) rows.push([labels[i], means[i]]);
    const csv = rows.map(r => r.join(',')).join('\n');
    const blob = new Blob([csv], {type:'text/csv'});
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href = url; a.download = 'bench_points.csv'; document.body.appendChild(a); a.click(); a.remove(); URL.revokeObjectURL(url);
}

async function loadSavedRuns() {
    try {
        const res = await fetch('/api/treesort/benchmarks');
        const runs = await res.json();
        const sel = document.getElementById('savedRuns');
        sel.innerHTML = '';
        runs.forEach(r => {
            const when = r.createdAt || r.creadoEn || r.created || '';
            const opt = document.createElement('option'); opt.value = r.id; opt.innerText = `#${r.id} - ${when}`; sel.appendChild(opt);
        });
    } catch (e) { console.error(e); }
}

async function loadSelectedRun() {
    const sel = document.getElementById('savedRuns');
    const id = sel.value; if (!id) return;
    const res = await fetch('/api/treesort/benchmark/' + id); const data = await res.json();
    const raw = data.resultJson || data.resultadoJson || data.result || data.resultado;
    const result = typeof raw === 'string' ? JSON.parse(raw) : raw;
    const points = result.points || [];
    const labels = points.map(p => p.n);
    const means = points.map(p => (p.mean / 1e6));
    const stds = points.map(p => (p.std / 1e6));
    renderChart(labels, means, stds);
    // show AI analysis if present
    const ai = data.aiAnalysis || data.analisisAi || data.analisis || null;
    if (ai) {
        document.getElementById('resultInfo').innerText = ai;
    }
}

async function analyzeSelectedRun() {
    const sel = document.getElementById('savedRuns');
    const id = sel.value; if (!id) return;
    const res = await fetch('/api/treesort/benchmark/' + id + '/analyze', { method:'POST' });
    const data = await res.json();
    alert('Análisis AI guardado en run ' + data.id);
    if (data.aiAnalysis) document.getElementById('resultInfo').innerText = data.aiAnalysis;
}

// read query param input and optionally auto-run
function getQueryInput() {
    const params = new URLSearchParams(window.location.search);
    return params.get('input');
}

window.addEventListener('load', async () => {
    await loadSavedRuns();
    const input = getQueryInput();
    if (input) {
        const modeEl = document.getElementById('mode');
        if (modeEl) modeEl.value = 'manual';
        const manualEl = document.getElementById('manualArray');
        if (manualEl) manualEl.value = decodeURIComponent(input);
        // if auto flag present, run automatically
        const params = new URLSearchParams(window.location.search);
        const auto = params.get('auto');
        if (auto === '1' || document.getElementById('autoRun').checked) {
            runBenchmark();
        }
    }
    // toggle manualArray visibility when mode changes
    const modeSel = document.getElementById('mode');
    const manualWrapper = document.getElementById('manualArray');
    if (modeSel) {
        function updateManualVisibility() {
            const m = modeSel.value;
            if (manualWrapper) manualWrapper.style.display = (m === 'manual') ? 'inline-block' : 'none';
        }
        modeSel.addEventListener('change', updateManualVisibility);
        updateManualVisibility();
    }
    // load aggregated data on page load
    await loadAggregated();
});

async function loadAggregated() {
    try {
        const res = await fetch('/api/treesort/benchmarks/aggregate');
        const data = await res.json();
        const pts = data.points || [];
        const labels = pts.map(p => p.n);
        const means = pts.map(p => ((p.meanNs || 0) / 1e6));
        // render aggregated as primary chart
        renderChart(labels, means, []);
    } catch (e) { console.error('Error loading aggregated', e); }
}

// Heuristic analysis on the data currently displayed in the chart
function analyzeDisplayedData() {
    try {
        if (!chart) { alert('No hay datos en la gráfica.'); return; }
        const labels = chart.data.labels.map(v => Number(v));
        const means = chart.data.datasets[0].data.map(v => Number(v)); // ms
        if (!labels.length) { alert('No hay puntos para analizar.'); return; }

        // fit c for n*log(n) and n^2
        function fitC(farr) {
            let num = 0, den = 0;
            for (let i=0;i<labels.length;i++) { const f = farr(labels[i]); num += f * means[i]; den += f*f; }
            return den === 0 ? 0 : num/den;
        }
        const cNLog = fitC(n => n * Math.log(n));
        const cN2 = fitC(n => n * n);

        // compute R2
        function r2(pred) {
            const meanY = means.reduce((a,b)=>a+b,0)/means.length;
            let ssRes=0, ssTot=0;
            for (let i=0;i<means.length;i++) { ssRes += Math.pow(means[i]-pred[i],2); ssTot += Math.pow(means[i]-meanY,2); }
            return 1 - (ssRes/ssTot || 1);
        }
        const predNLog = labels.map(n => cNLog * n * Math.log(n));
        const predN2 = labels.map(n => cN2 * n * n);
        const r2NLog = r2(predNLog);
        const r2N2 = r2(predN2);

        let text = 'Análisis heurístico (cliente):\n';
        text += `Ajuste n·log(n): c = ${cNLog.toExponential(6)} (unidad: ms / (n·log n))\n`;
        text += `Ajuste n^2: c = ${cN2.toExponential(6)} (unidad: ms / n^2)\n`;
        text += `R^2 n·log(n): ${r2NLog.toFixed(4)} | R^2 n^2: ${r2N2.toFixed(4)}\n`;
        if (r2NLog > r2N2) text += 'Interpretación: la curva se ajusta mejor a O(n·log n) en este rango.\n';
        else if (r2N2 > r2NLog) text += 'Interpretación: la curva se ajusta mejor a O(n^2) en este rango.\n';
        else text += 'Interpretación: ambos modelos tienen ajuste similar; se requieren más datos.\n';

        document.getElementById('resultInfo').innerText = text;
    } catch (e) { alert('Error en análisis: ' + e.message); }
}

