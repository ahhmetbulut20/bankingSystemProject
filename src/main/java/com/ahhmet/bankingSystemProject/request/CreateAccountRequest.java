package com.ahhmet.bankingSystemProject.request;

public class CreateAccountRequest {
	int bankId;
	String type;
	
	public int getBankId() {
		return bankId;
	}
	public void setBankId(int bankId) {
		this.bankId = bankId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
		
}
