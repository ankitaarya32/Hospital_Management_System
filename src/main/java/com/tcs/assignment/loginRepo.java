package com.tcs.assignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


public interface loginRepo extends CrudRepository<login,Integer>{
	//List<login> findByusernameAndPassword(@Param("username")String username,@Param("password") String password);
	   //@Query("SELECT e from login e where e.username=:uname AND e.password =:pass")
	List<login> findByUsernameAndPassword(String username,String password);
}
