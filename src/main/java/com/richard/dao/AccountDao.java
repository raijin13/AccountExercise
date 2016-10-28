package com.richard.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.richard.entity.Account;

@Repository
public interface AccountDao extends CrudRepository<Account, Long> {
	
	@Query(value = "SELECT * FROM Account WHERE id = :id FOR UPDATE", nativeQuery=true)
	public Account lockAccount(@Param("id") Long id);
}
