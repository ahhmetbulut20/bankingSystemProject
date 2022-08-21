package com.ahhmet.bankingSystemProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Forbidden;

import com.ahhmet.bankingSystemProject.jwtSecurity.JwtTokenUtil;
import com.ahhmet.bankingSystemProject.request.LoginRequest;
import com.ahhmet.bankingSystemProject.response.AuthResponse;
import com.ahhmet.bankingSystemProject.response.ForbiddenResponse;
import com.ahhmet.bankingSystemProject.service.UserService;

@CrossOrigin()
@RestController
@RequestMapping("/auth")
public class LoginController {
	
	private AuthenticationManager authenticationManager;
	private JwtTokenUtil jwtTokenUtil;
	private UserService userDetailsService;
	
	@Autowired
	public LoginController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserService userDetailService)
	{
		this.authenticationManager=authenticationManager;
		this.jwtTokenUtil=jwtTokenUtil;
		this.userDetailsService=userDetailService;
	}
	
	@PostMapping(path="")
	public ResponseEntity<?>login(@RequestBody LoginRequest loginRequest){
		
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			
		} catch (BadCredentialsException e) {
			return ResponseEntity.badRequest().build();
		} catch (DisabledException e) {
			ForbiddenResponse forbiddenResponse=new ForbiddenResponse();
			forbiddenResponse.setSuccess(false);
			forbiddenResponse.setMessage(e.getMessage());
			return new ResponseEntity<>(forbiddenResponse,HttpStatus.FORBIDDEN);
		}
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
		System.out.println(userDetails);
		final String token = jwtTokenUtil.generateToken(userDetails);
		AuthResponse authResponse=new AuthResponse();
		authResponse.setSuccess(true);
		authResponse.setMessage("Logged-In Successfully");
		authResponse.setToken(token);
		return ResponseEntity.ok().body(authResponse);
			
	}
	
}

