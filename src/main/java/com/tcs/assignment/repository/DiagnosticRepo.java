package com.tcs.assignment.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.assignment.bean.PatientDaignostic;

public interface DiagnosticRepo extends CrudRepository<PatientDaignostic, Integer>{
	@Query(value = "SELECT max(diag_pait_id) FROM PatientDaignostic")
	public int max();
}
