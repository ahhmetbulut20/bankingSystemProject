package com.ahhmet.bankingSystemProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahhmet.bankingSystemProject.exceptions.ExistingBankException;
import com.ahhmet.bankingSystemProject.exceptions.ExistingUsernameException;
import com.ahhmet.bankingSystemProject.model.Bank;
import com.ahhmet.bankingSystemProject.model.User;
import com.ahhmet.bankingSystemProject.request.CreateBankRequest;
import com.ahhmet.bankingSystemProject.response.AlreadyExistingResponse;
import com.ahhmet.bankingSystemProject.response.BankCreatedResponse;
import com.ahhmet.bankingSystemProject.response.UserCreatedResponse;
import com.ahhmet.bankingSystemProject.service.BankService;

@RestController
@RequestMapping("/api")
public class BankController {
	
	private BankService bankService;
	@Autowired
	public BankController(BankService bankService) {
		this.bankService=bankService;
	}
	
	@PostMapping("/bank")
	public ResponseEntity<?>createBank(@RequestBody CreateBankRequest createBankRequest){
		Bank bank=new Bank();
		try {
			bank = bankService.createBank(createBankRequest.getName());
		}
		catch (ExistingBankException e) {
			AlreadyExistingResponse alreadyExistingResponse=new AlreadyExistingResponse();
			alreadyExistingResponse.setSuccess(false);
			alreadyExistingResponse.setMessage("Given Name Already Used : "+createBankRequest.getName());
			return new ResponseEntity<>(alreadyExistingResponse,HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		BankCreatedResponse bankCreatedResponse=new BankCreatedResponse();
		bankCreatedResponse.setBank(bank);
		bankCreatedResponse.setSuccess(true);
		bankCreatedResponse.setMessage("Created Succesfully");
		return new ResponseEntity<>(bankCreatedResponse,HttpStatus.CREATED);
		
	}
	
}
