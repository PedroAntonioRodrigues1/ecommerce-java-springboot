package com.retalho.vendas.config;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//Indica que essa classe é um componente gerenciado pelo Spring
//e pode ser injetada em outros lugares.
@Service
public class AuthSucessHandlerImpl implements AuthenticationSuccessHandler {

 /**
  * Método chamado automaticamente pelo Spring Security
  * quando a autenticação (login) é bem-sucedida.
  *
  * @param request        Objeto com os dados da requisição HTTP.
  * @param response       Objeto para enviar a resposta HTTP.
  * @param authentication Objeto que contém os detalhes do usuário autenticado,
  *                        incluindo username, roles, etc.
  */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
	                                     HttpServletResponse response,
	                                     Authentication authentication)
	                                     throws IOException, ServletException {
	    // Pega todas as autoridades (permissões/roles) do usuário autenticado.
	    // Exemplo: [ROLE_ADMIN, ROLE_USER]
	    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	    
	    // Converte a lista de GrantedAuthority para um Set<String> com os nomes das roles.
	    // Exemplo: {"ROLE_ADMIN", "ROLE_USER"}
	    Set<String> roles = AuthorityUtils.authorityListToSet(authorities);
	    
	    // Se o usuário tiver a role de administrador...
	    if (roles.contains("ROLE_ADMIN")) {
	        // ...redireciona para a área administrativa
	        response.sendRedirect("/admin/");
	    } else {
	        // Caso contrário, redireciona para a página inicial
	        response.sendRedirect("/");
	    }
	}

}
