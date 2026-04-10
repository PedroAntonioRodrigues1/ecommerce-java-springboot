package com.retalho.vendas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.retalho.vendas.model.UserDtls;
import com.retalho.vendas.repository.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	
	@Autowired
	private UserRepository userRepository;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDtls user = userRepository.findByLoginEmail(username);
		if(user==null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new CustomUser(user);
	}

}
