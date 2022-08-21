package com.ahhmet.bankingSystemProject.service;

import java.sql.Timestamp;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ahhmet.bankingSystemProject.currency.CurrencyInterface;
import com.ahhmet.bankingSystemProject.exceptions.InsufficientBalanceException;
import com.ahhmet.bankingSystemProject.exceptions.InvalidAccountTypeException;
import com.ahhmet.bankingSystemProject.exceptions.OperationFailedException;
import com.ahhmet.bankingSystemProject.model.Account;
import com.ahhmet.bankingSystemProject.repository.IAccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class AccountService{
	
	private IAccountRepository accountRepository;
	private CurrencyInterface currency;
	@Autowired
	public AccountService(IAccountRepository accountRepository, CurrencyInterface currency) {
		this.accountRepository=accountRepository;
		this.currency=currency;
	}
	
	public Account createAccount(int bankId, String type, int userId) throws InvalidAccountTypeException, OperationFailedException {
		
		if(type.equals("TL") || type.equals("Dolar") || type.equals("Altın")) {
			Account account=new Account();
			Random rnd = new Random();
		    account.setAccountNumber(rnd.nextInt(999999999)+1000000000);
			account.setBankId(bankId);
			account.setUserId(userId);
			account.setBalance(0);
			account.setType(type);
			account.setDeleted(false);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			account.setCreationDate(timestamp);
			account.setLastUpdateDate(timestamp);
			if(accountRepository.createAccount(account))
				return accountRepository.findByAccountNumber(account.getAccountNumber());
			else
				throw new OperationFailedException();
		}
		else
			throw new InvalidAccountTypeException();
	}
	
	public Account findByAccountNumber(int accountNumber){
		Account account=accountRepository.findByAccountNumber(accountNumber);
		if(account!=null)
			return account;
		
		throw new NullPointerException();
	}
	
	public boolean removeAccount(int accountNumber) {
		int removedAccount=accountRepository.removeAccount(accountNumber);
		if(removedAccount>0)
			return true;
		
		return false;
	}
	
	public Account increaseBalance(int accountNumber,double balance) throws OperationFailedException {
		Account account=accountRepository.findByAccountNumber(accountNumber);
		account.setBalance(account.getBalance()+balance);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		account.setLastUpdateDate(timestamp);
		int increasedBalance=accountRepository.updateBalance(account);
		if(increasedBalance>0) {
			return accountRepository.findByAccountNumber(accountNumber);
		}
		throw new OperationFailedException();
	}
	
	@Transactional
	public boolean transferBalance(int senderAccountNumber, int receivedAccountNumber, double balance) throws InsufficientBalanceException{
		Account senderAccount=accountRepository.findByAccountNumber(senderAccountNumber);
		Account receivedAccount=accountRepository.findByAccountNumber(receivedAccountNumber);
		
		double totalDecliningBalance=balance;
		if(senderAccount.getBankId()!=receivedAccount.getBankId())
			totalDecliningBalance=this.calculateEft(senderAccount.getType(), receivedAccount.getType())+balance;
		
		if(senderAccount.getBalance()<totalDecliningBalance) {
			throw new InsufficientBalanceException();
		}
		
		senderAccount.setBalance(senderAccount.getBalance()-totalDecliningBalance);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		senderAccount.setLastUpdateDate(timestamp);
		int updatedSenderAccount=accountRepository.updateBalance(senderAccount);
	
		double transferredAmount=balance;
		if(!senderAccount.getType().equals(receivedAccount.getType())) {
			try {
				transferredAmount=currency.exchange(balance,senderAccount.getType(),receivedAccount.getType());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return false;
			}
		}
		receivedAccount.setBalance(receivedAccount.getBalance()+transferredAmount);
		receivedAccount.setLastUpdateDate(timestamp);
		int updatedReceivedAccount= accountRepository.updateBalance(receivedAccount);
		
		if(updatedSenderAccount>0 && updatedReceivedAccount>0)
			return true;
		
		return false;
	}
	
	public double calculateEft(String senderType,String receivedType) {
		if(senderType==receivedType) 
			return 0;
		
		if(senderType.equals("TL")) 
			return 3;
		
		else if(senderType.equals("Dolar")) 
			return 1;
		
		else
			return 0;
	}
	
}
