package com.tcs.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.assignment.bean.MedicineMaster;
@Repository
public interface Medicinerepojpa extends JpaRepository<MedicineMaster, Integer> {

}
