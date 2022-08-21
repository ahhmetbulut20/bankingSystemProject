package com.ahhmet.bankingSystemProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ahhmet.bankingSystemProject.exceptions.ExistingBankException;
import com.ahhmet.bankingSystemProject.model.Bank;
import com.ahhmet.bankingSystemProject.repository.IBankRepository;

@Component
public class BankService {
	
	private IBankRepository bankRepository;
	@Autowired
	public BankService(IBankRepository bankRepository)
	{
		this.bankRepository=bankRepository;
	}
	
	public Bank createBank(String name) throws ExistingBankException {
		Bank bank = bankRepository.findByName(name);
		if(bank==null) {
			if(bankRepository.createBank(name)) {
				bank=bankRepository.findByName(name);
				return bank;
			}
		}
		throw new ExistingBankException();
	}
	
}
