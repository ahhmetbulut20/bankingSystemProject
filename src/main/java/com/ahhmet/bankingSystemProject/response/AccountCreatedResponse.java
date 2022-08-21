package com.ahhmet.bankingSystemProject.response;

import com.ahhmet.bankingSystemProject.model.Account;

public class AccountCreatedResponse {
	private boolean success;
	private String message;
	private Account account;
	
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
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
}
