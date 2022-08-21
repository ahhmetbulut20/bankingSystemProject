package com.ahhmet.bankingSystemProject.response;

import com.ahhmet.bankingSystemProject.model.Bank;

public class BankCreatedResponse {
	private boolean success;
	private String message;
	private Bank bank;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Bank getBank() {
		return bank;
	}
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
}
