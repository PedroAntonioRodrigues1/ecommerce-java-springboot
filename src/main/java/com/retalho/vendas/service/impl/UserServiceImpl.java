package com.retalho.vendas.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.retalho.vendas.model.UserDtls;
import com.retalho.vendas.repository.UserRepository;
import com.retalho.vendas.service.UserService;
import com.retalho.vendas.util.AppConstant;


@Service
public class UserServiceImpl  implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDtls saveUser(UserDtls user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		String encodeSenha = passwordEncoder.encode(user.getSenha());
		user.setSenha(encodeSenha);;
		UserDtls saveUser = userRepository.save(user);
		return saveUser; 	
	}

	@Override
	public UserDtls getUserbyEmail(String email) {
		return userRepository.findByLoginEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		return userRepository.findByRole(role);
	
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		Optional<UserDtls> byId = userRepository.findById(id);
		if(byId.isPresent()) {
			UserDtls userDtls = byId.get();
			userDtls.setIsEnable(status);
			userRepository.save(userDtls);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		
		int attempt = user.getFailedAttempt()+1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
		
	}

	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);
	}

	@Override
	public Boolean unlockAccountTimeExpired(UserDtls user) {
		if (user.getLockTime() == null) {
	        return false; // nunca foi bloqueado, então não há o que desbloquear
	    }
		long lockTime = user.getLockTime().getTime();
		long unlockTime = lockTime+AppConstant.UNLOCK_DURATION_TIME;
		long currentTime = System.currentTimeMillis();
		if(unlockTime<currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true; 
		}
		return false;
	}

	@Override
	public void resetAttempt(Integer userId) {
	
		
	}

	@Override
	public void updateResetToken(String email, String resetToken) {
		UserDtls byLoginEmail = userRepository.findByLoginEmail(email);
		byLoginEmail.setResetToken(resetToken);
		userRepository.save(byLoginEmail);
		
	}

	@Override
	public UserDtls getUserbyToken(String token) {
		return userRepository.findByResetToken(token);
	}

	@Override
	public UserDtls updateUser(UserDtls user) {
		return userRepository.save(user);
		
	}

	@Override
	public UserDtls updateUserProfile(UserDtls user) {
		UserDtls dbUser = userRepository.findById(user.getId()).get();
		if(!ObjectUtils.isEmpty(dbUser)) {
			dbUser.setNome(user.getNome());
			dbUser.setTelefone(user.getTelefone());
			dbUser= userRepository.save(dbUser);
		}
		return dbUser;
	}
	

	@Override
	public UserDtls saveAdmin(UserDtls user) {
		user.setRole("ROLE_ADMIN");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		String encodeSenha = passwordEncoder.encode(user.getSenha());
		user.setSenha(encodeSenha);
		UserDtls saveUser = userRepository.save(user);
		return saveUser; 	
	}

	@Override
	public Boolean existsEmail(String email) {
		
		return userRepository.existsByLoginEmail(email);
	}

}
