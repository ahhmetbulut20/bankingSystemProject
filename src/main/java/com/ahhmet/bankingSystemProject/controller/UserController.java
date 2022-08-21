package com.ahhmet.bankingSystemProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ahhmet.bankingSystemProject.exceptions.ExistingEmailException;
import com.ahhmet.bankingSystemProject.exceptions.ExistingUsernameException;
import com.ahhmet.bankingSystemProject.model.User;
import com.ahhmet.bankingSystemProject.request.EnabledRequest;
import com.ahhmet.bankingSystemProject.request.RegisterRequest;
import com.ahhmet.bankingSystemProject.response.AlreadyExistingResponse;
import com.ahhmet.bankingSystemProject.response.EnabledUserResponse;
import com.ahhmet.bankingSystemProject.response.UserCreatedResponse;
import com.ahhmet.bankingSystemProject.service.UserService;

@CrossOrigin()
@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private BCryptPasswordEncoder passwordEncoder;
	private UserService userService;
	
	@Autowired
	public UserController(UserService userService, BCryptPasswordEncoder passwordEncoder) {
		this.userService=userService;
		this.passwordEncoder=passwordEncoder;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?>createUser(@RequestBody RegisterRequest registerRequest){
		User user=new User();
		try {
			user = userService.register(registerRequest.getUsername(), registerRequest.getEmail(), passwordEncoder.encode(registerRequest.getPassword()));
		}
		catch (ExistingUsernameException e) {
			AlreadyExistingResponse alreadyExistingResponse=new AlreadyExistingResponse();
			alreadyExistingResponse.setSuccess(false);
			alreadyExistingResponse.setMessage("Given Name Already Used : "+registerRequest.getUsername());
			return new ResponseEntity<>(alreadyExistingResponse,HttpStatus.UNPROCESSABLE_ENTITY);
		}
		catch (ExistingEmailException e) {
			AlreadyExistingResponse alreadyExistingResponse=new AlreadyExistingResponse();
			alreadyExistingResponse.setSuccess(false);
			alreadyExistingResponse.setMessage("Given Email Already Used : "+registerRequest.getEmail());
			return new ResponseEntity<>(alreadyExistingResponse,HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		UserCreatedResponse userCreatedResponse=new UserCreatedResponse();
		userCreatedResponse.setUser(user);
		userCreatedResponse.setSuccess(true);
		userCreatedResponse.setMessage("Created Succesfully");
		return new ResponseEntity<>(userCreatedResponse,HttpStatus.CREATED);
		
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<?>enableUser(@RequestBody EnabledRequest enabledRequest, @PathVariable int id)
	{
		boolean enableResult=userService.activateDeactivateUser(id, enabledRequest.isEnabled());
		if(enableResult) {
			EnabledUserResponse enabledUserResponse=new EnabledUserResponse();
			enabledUserResponse.setStatus("success");
			if(enabledRequest.isEnabled()) {
				enabledUserResponse.setMessage("User Enabled");
			}
			else {
				enabledUserResponse.setMessage("User Disabled");
			}
		
			return ResponseEntity.ok().body(enabledUserResponse);
		}
		return new ResponseEntity<>("The requested User was not found",HttpStatus.BAD_REQUEST);
	}
	
}
