package com.ahhmet.bankingSystemProject.repository;

import org.apache.ibatis.annotations.Mapper;

import com.ahhmet.bankingSystemProject.model.User;

@Mapper
public interface IUserRepository {
	public boolean createUser(User user);
	public User findByUsername(String username);
	public User findByEmail(String email);
	public User loadUserByUsername(String username);
	public int activateDeactivateUser(int id, boolean enabled);
}
