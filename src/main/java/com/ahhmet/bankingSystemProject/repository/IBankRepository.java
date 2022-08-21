package com.ahhmet.bankingSystemProject.repository;

import org.apache.ibatis.annotations.Mapper;

import com.ahhmet.bankingSystemProject.model.Bank;

@Mapper
public interface IBankRepository {
	public boolean createBank(String name);
	public Bank findByName(String name);
}
