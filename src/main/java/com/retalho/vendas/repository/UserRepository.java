package com.retalho.vendas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retalho.vendas.model.UserDtls;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {
	
	public UserDtls findByLoginEmail(String loginEmail);

	public List<UserDtls> findByRole(String role);
	
	public UserDtls findByResetToken(String token);
	
	public Boolean existsByLoginEmail(String email);

	public Optional<UserDtls> findById(Long userId);

}
