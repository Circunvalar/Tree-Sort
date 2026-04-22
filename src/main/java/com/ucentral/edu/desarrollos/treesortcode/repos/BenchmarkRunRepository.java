package com.ucentral.edu.desarrollos.treesortcode.repos;

import com.ucentral.edu.desarrollos.treesortcode.modelos.BenchmarkRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BenchmarkRunRepository extends JpaRepository<BenchmarkRun, Long> {

	@Query("select new com.ucentral.edu.desarrollos.treesortcode.dtos.BenchmarkSummary(b.id, b.createdAt) from BenchmarkRun b order by b.createdAt desc")
	java.util.List<com.ucentral.edu.desarrollos.treesortcode.dtos.BenchmarkSummary> findAllSummaries();

	@Query("select b.resultJson from BenchmarkRun b")
	java.util.List<String> findAllResultJsons();
}

