package com.retalho.vendas.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.retalho.vendas.model.UserDtls;


public class CustomUser implements UserDetails{

	private UserDtls user;
	
	
	/**
	 * Construtor que recebe um objeto UserDtls e o associa a esta instância de CustomUser.
	 * Isso permite adaptar os dados do usuário (vindos do banco) para o formato que o 
	 * Spring Security entende (UserDetails), facilitando o processo de autenticação.
	 *
	 * @param user objeto contendo as informações do usuário da aplicação
	 */
	public CustomUser(UserDtls user) {
		super();
		this.user = user;
	}

	
	/**
	 * Retorna a lista de permissões (roles) do usuário para o Spring Security.
	 * 
	 * O Spring Security usa essas "authorities" para controlar o acesso a recursos
	 * protegidos, como endpoints e páginas. Cada autoridade normalmente representa
	 * um papel no sistema, por exemplo: "ROLE_ADMIN" ou "ROLE_USER".
	 *
	 * @return uma coleção contendo as permissões do usuário
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
		return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		return user.getSenha();
	}

	@Override
	public String getUsername() {
		return user.getLoginEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return user.getAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getIsEnable();
	}
}
