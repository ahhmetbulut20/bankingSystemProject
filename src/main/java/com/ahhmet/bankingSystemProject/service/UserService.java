package com.ahhmet.bankingSystemProject.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ahhmet.bankingSystemProject.exceptions.ExistingEmailException;
import com.ahhmet.bankingSystemProject.exceptions.ExistingUsernameException;
import com.ahhmet.bankingSystemProject.jwtSecurity.MyUser;
import com.ahhmet.bankingSystemProject.model.User;
import com.ahhmet.bankingSystemProject.repository.IUserRepository;

@Component
public class UserService implements UserDetailsService{
	
	private IUserRepository userRepository;
	@Autowired
	public UserService(IUserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	public User register(String username,String email,String password) throws ExistingUsernameException, ExistingEmailException {
		User user=userRepository.findByUsername(username);
		if(user==null) {
			user=userRepository.findByEmail(email);
			if(user==null) {
				user=new User();
				user.setAuthorities("CREATE_ACCOUNT");
				user.setPassword(password);
				user.setUsername(username);
				user.setEmail(email);
				user.setEnabled(false);
				if(userRepository.createUser(user)) {
					user=userRepository.findByUsername(username);
					return user;
				}
			}
			throw new ExistingEmailException();
		}
		throw new ExistingUsernameException();
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userDetails = userRepository.loadUserByUsername(username);
		String authorities= userDetails.getAuthorities();
		String[] parsed = authorities.split(",");
		List<GrantedAuthority> grantedAuthority=new ArrayList<GrantedAuthority>(); 
		for(int i=0;i<parsed.length;i++) {
			grantedAuthority.add(new SimpleGrantedAuthority(parsed[i]));
		}
		MyUser myUser=new MyUser(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),userDetails.getPassword(),userDetails.isEnabled(),grantedAuthority);
		return myUser;
	}
	
	public boolean activateDeactivateUser(int id, boolean enabled) {
		int enabledUser=userRepository.activateDeactivateUser(id, enabled);
		if(enabledUser>0)
			return true;
		
		return false;
	} 
}

