package com.tcs.assignment.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tcs.assignment.bean.login;


public interface loginRepo extends CrudRepository<login,Integer>{
	//List<login> findByusernameAndPassword(@Param("username")String username,@Param("password") String password);
	   //@Query("SELECT e from login e where e.username=:uname AND e.password =:pass")
	List<login> findByUsernameAndPassword(String username,String password);
}
