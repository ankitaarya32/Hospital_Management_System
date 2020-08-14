package com.tcs.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.tcs.assignment.bean.MedicineMaster;

public interface MastermedRepo extends JpaRepository<MedicineMaster, Integer>{

}
