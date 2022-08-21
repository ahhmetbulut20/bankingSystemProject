package com.ahhmet.bankingSystemProject.request;

public class TransferRequest {
	double amount;
	int receivedAccountNumber;
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getReceivedAccountNumber() {
		return receivedAccountNumber;
	}
	public void setReceivedAccountNumber(int receivedAccountNumber) {
		this.receivedAccountNumber = receivedAccountNumber;
	}
	
}
