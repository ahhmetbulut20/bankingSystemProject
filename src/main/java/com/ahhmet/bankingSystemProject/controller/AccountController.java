package com.ahhmet.bankingSystemProject.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahhmet.bankingSystemProject.response.AccountTransferResponse;
import com.ahhmet.bankingSystemProject.exceptions.InsufficientBalanceException;
import com.ahhmet.bankingSystemProject.exceptions.InvalidAccountTypeException;
import com.ahhmet.bankingSystemProject.exceptions.OperationFailedException;
import com.ahhmet.bankingSystemProject.jwtSecurity.MyUser;
import com.ahhmet.bankingSystemProject.model.Account;
import com.ahhmet.bankingSystemProject.request.CreateAccountRequest;
import com.ahhmet.bankingSystemProject.request.IncreaseBalanceRequest;
import com.ahhmet.bankingSystemProject.request.TransferRequest;
import com.ahhmet.bankingSystemProject.response.AccessDeniedResponse;
import com.ahhmet.bankingSystemProject.response.AccountCreatedResponse;
import com.ahhmet.bankingSystemProject.response.DeletedAccountResponse;
import com.ahhmet.bankingSystemProject.response.IncreasedBalanceResponse;
import com.ahhmet.bankingSystemProject.response.InsufficientBalanceResponse;
import com.ahhmet.bankingSystemProject.response.InvalidAccountTypeResponse;
import com.ahhmet.bankingSystemProject.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class AccountController {
	
	private KafkaTemplate<String,String>producer;
	private AccountService accountService;
	@Autowired
	public AccountController (AccountService accountService, KafkaTemplate<String,String>producer) {
		this.accountService=accountService;
		this.producer=producer;
	}
	
	@PostMapping("")
	public ResponseEntity<?>createAccount(@RequestBody CreateAccountRequest createAccountRequest){
		MyUser myUser = (MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account account=new Account();
		try {
			account=accountService.createAccount(createAccountRequest.getBankId(), createAccountRequest.getType(), myUser.getId());
		}
		catch(InvalidAccountTypeException e) {
			InvalidAccountTypeResponse invalidAccountTypeResponse=new InvalidAccountTypeResponse();
			invalidAccountTypeResponse.setSuccess(false);
			invalidAccountTypeResponse.setMessage("Invalid Account Type : "+createAccountRequest.getType());
			return new ResponseEntity<>(invalidAccountTypeResponse,HttpStatus.PRECONDITION_FAILED);
		}
		catch(OperationFailedException e) {
			return new ResponseEntity<>("The requested operation is failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		AccountCreatedResponse accountCreatedResponse=new AccountCreatedResponse();
		accountCreatedResponse.setSuccess(true);
		accountCreatedResponse.setMessage("Account Created");
		accountCreatedResponse.setAccount(account);
		return new ResponseEntity<>(accountCreatedResponse,HttpStatus.CREATED);
	}
	
	@GetMapping("/{accountNumber}")
	public ResponseEntity<?>detailOfAccount(@PathVariable int accountNumber)
	{
		MyUser myUser = (MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account account=accountService.findByAccountNumber(accountNumber);
		if(account.getUserId()==myUser.getId()) {
			return ResponseEntity.ok().lastModified(ZonedDateTime.ofInstant(account.getLastUpdateDate().toInstant(), ZoneId.systemDefault())).body(account);
		}
		AccessDeniedResponse accessDeniedResponse=new AccessDeniedResponse();
		accessDeniedResponse.setMessage("Access Denied");
		return new ResponseEntity<>(accessDeniedResponse,HttpStatus.UNAUTHORIZED);
	}
	
	@DeleteMapping("/{accountNumber}")
	public ResponseEntity<?>deleteAccount(@PathVariable int accountNumber)
	{
		boolean deletedAccount=accountService.removeAccount(accountNumber);
		if(deletedAccount) {
			DeletedAccountResponse deletedAccountResponse=new DeletedAccountResponse();
			deletedAccountResponse.setSuccess(true);
			deletedAccountResponse.setMessage("Account Deleted");
			return new ResponseEntity<>(deletedAccountResponse,HttpStatus.OK);
		}
		
		return new ResponseEntity<>("The requested Account was not found",HttpStatus.BAD_REQUEST);
	}
	
	@PatchMapping("/{accountNumber}")
	public ResponseEntity<?>increaseBalance(@RequestBody IncreaseBalanceRequest increaseBalanceRequest, @PathVariable int accountNumber)
	{
		MyUser myUser = (MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account account=new Account();
		try {
			account=accountService.findByAccountNumber(accountNumber);
		}
		catch(NullPointerException e) {
			return new ResponseEntity<>("The requested Account was not found",HttpStatus.BAD_REQUEST);
		}
		
		if(account.getUserId()==myUser.getId()) {
			try {
				account=accountService.increaseBalance(accountNumber, increaseBalanceRequest.getAmount());
			}
			catch(OperationFailedException e) {
				return new ResponseEntity<>("The requested operation is failed",HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String message=accountNumber+", "+increaseBalanceRequest.getAmount()+" : deposited";
			producer.send("logs",message);
			
			IncreasedBalanceResponse increasedBalanceResponse=new IncreasedBalanceResponse();
			increasedBalanceResponse.setSuccess(true);
			increasedBalanceResponse.setMessage("Deposit Successfully");
			increasedBalanceResponse.setBalance(account.getBalance());
			return new ResponseEntity<>(increasedBalanceResponse,HttpStatus.OK);
		}
		
		AccessDeniedResponse accessDeniedResponse=new AccessDeniedResponse();
		accessDeniedResponse.setMessage("Access Denied");
		return new ResponseEntity<>(accessDeniedResponse,HttpStatus.UNAUTHORIZED);
		
	}
	
	@PatchMapping("/transfer/{accountNumber}")
	public ResponseEntity<?>transferBalance(@RequestBody TransferRequest transferRequest, @PathVariable int accountNumber)
	{
		MyUser myUser = (MyUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Account account=new Account();
		try {
			account=accountService.findByAccountNumber(accountNumber);
		}
		catch(NullPointerException e) {
			return new ResponseEntity<>("The requested Account was not found",HttpStatus.BAD_REQUEST);
		}
		
		if(account.getUserId()!=myUser.getId()) {
			AccessDeniedResponse accessDeniedResponse=new AccessDeniedResponse();
			accessDeniedResponse.setMessage("Access Denied");
			return new ResponseEntity<>(accessDeniedResponse,HttpStatus.UNAUTHORIZED);
		}
		
		boolean transferResult;
		try {
			transferResult=accountService.transferBalance(accountNumber,transferRequest.getReceivedAccountNumber() ,transferRequest.getAmount());
		}
		catch(InsufficientBalanceException e) {
			InsufficientBalanceResponse insufficientBalanceResponse=new InsufficientBalanceResponse();
			insufficientBalanceResponse.setMessage("Insufficient Balance");
			return new ResponseEntity<>(insufficientBalanceResponse,HttpStatus.BAD_REQUEST);
		}
		
		if(transferResult) {
			
			String message=transferRequest.getAmount()+", "+accountNumber+" to "+transferRequest.getReceivedAccountNumber()+" : transferred";
			producer.send("logs",message);
			
			AccountTransferResponse accountTransferResponse = new AccountTransferResponse();
			accountTransferResponse.setMessage("Transferred Successfully");
			return ResponseEntity.ok().body(accountTransferResponse);
		}
		
		return new ResponseEntity<>("The requested operation is failed",HttpStatus.INTERNAL_SERVER_ERROR);		
		
	}
}