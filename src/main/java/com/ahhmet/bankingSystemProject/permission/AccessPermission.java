package com.ahhmet.bankingSystemProject.permission;

import org.springframework.beans.factory.annotation.Autowired;

import com.ahhmet.bankingSystemProject.model.Account;
import com.ahhmet.bankingSystemProject.repository.IAccountRepository;

public class AccessPermission {
	
	private IAccountRepository accountRepository;
	@Autowired
	public AccessPermission(IAccountRepository accountRepository) {
		this.accountRepository=accountRepository;
	}
	
	public boolean permissionControlByAccountNumber(int accountNumber, int userId) {
		Account account=accountRepository.findByAccountNumber(accountNumber);
		if(account!=null) {
			if(account.getUserId() == userId)
				return true;
			
			return false;
		}
			
		throw new NullPointerException();
	}
}
