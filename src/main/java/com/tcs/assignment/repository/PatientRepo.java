package com.tcs.assignment.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.assignment.bean.Patient;

@Repository
public interface PatientRepo extends CrudRepository<Patient, Integer> {
	@Query(value = "SELECT max(patientId) FROM Patient")
	public int max();
	
	
	
	Patient findByPatientId(int patientId);
}
