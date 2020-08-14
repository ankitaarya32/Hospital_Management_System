package com.tcs.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.assignment.bean.MedicineMaster;

public interface MastermedRepo extends JpaRepository<MedicineMaster, Integer>{

}
