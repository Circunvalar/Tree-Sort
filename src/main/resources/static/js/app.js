let steps = [];
let current = 0;
let playing = false;
let interval = null;

async function cargar() {
    const input = document.getElementById("input").value;

    const arr = input.split(/[\s,]+/)
        .map(n => parseInt(n))
        .filter(n => !isNaN(n));

    const res = await fetch("http://localhost:8080/api/treesort/steps", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(arr)
    });

    steps = await res.json();
    current = 0;
    render();
}

function render() {
    if (!steps.length) return;

    const step = steps[current];

    renderTree(step.tree, step.value);
    renderSorted(step.sorted);

    document.getElementById("estado").innerText =
        `${step.type.toUpperCase()} ${step.value ?? ''} | Paso ${current+1}/${steps.length}`;
}

function nextStep() {
    if (current < steps.length - 1) {
        current++;
        render();
    }
}

function prevStep() {
    if (current > 0) {
        current--;
        render();
    }
}

function play() {
    if (playing) return;
    playing = true;

    interval = setInterval(() => {
        if (current >= steps.length - 1) {
            pause();
            return;
        }
        current++;
        render();
    }, 500);
}

function pause() {
    playing = false;
    clearInterval(interval);
}

function renderTree(node, active) {
    const cont = document.getElementById("tree");
    cont.innerHTML = build(node, active);
}

function build(node, active) {
    if (!node) return "";

    return `
        <div>
            <div class="node ${node.value === active ? 'active' : ''}">
                ${node.value}
            </div>
            <div style="margin-left:20px;">
                ${build(node.left, active)}
                ${build(node.right, active)}
            </div>
        </div>
    `;
}

function renderSorted(arr) {
    document.getElementById("sorted").innerHTML =
        arr.map(n => `<span class="node">${n}</span>`).join("");
}

