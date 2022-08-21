package com.ahhmet.bankingSystemProject.repository;

import org.apache.ibatis.annotations.Mapper;

import com.ahhmet.bankingSystemProject.model.Account;

@Mapper
public interface IAccountRepository {
	public boolean createAccount(Account account);
	public Account findByAccountNumber(int accountNumber);
	public int removeAccount(int accountNumber);
	public int updateBalance(Account account);
}
