package com.tcs.assignment.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.assignment.bean.PatientMedicine;

public interface MedicineRepo extends CrudRepository<PatientMedicine,Integer > {
	@Query(value = "SELECT max(pat_med_id) FROM PatientMedicine")
	public int max();
}
