package com.retalho.vendas.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.retalho.vendas.model.UserDtls;
import com.retalho.vendas.repository.UserRepository;
import com.retalho.vendas.service.UserService;
import com.retalho.vendas.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService service;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	        AuthenticationException exception) throws IOException, ServletException {

	    // Pega o e-mail (username) que o usuário digitou no formulário de login
	    String email = request.getParameter("username");

	    // Tenta encontrar o usuário no banco de dados com base no e-mail fornecido
	    UserDtls user = userRepository.findByLoginEmail(email);

	    // 1. CASO: E-MAIL NÃO ENCONTRADO (usuário não existe)
	    // Este 'if' é crucial para evitar o NullPointerException.
	    if (user == null) {
	        // Se o usuário não for encontrado, criamos uma exceção genérica.
	        // Por segurança, nunca dizemos "E-mail não encontrado", apenas que os dados são inválidos.
	        exception = new BadCredentialsException("Usuário ou senha inválidos.");
	    } 
	    
	    // 2. CASO: E-MAIL ENCONTRADO, vamos verificar o estado da conta
	    else {
	        // Verifica se a conta do usuário está ativa e não está bloqueada
	        if (user.getIsEnable() && user.getAccountNonLocked()) {
	            
	            // Se a exceção original for 'BadCredentialsException', significa que a senha estava errada.
	            if (exception instanceof BadCredentialsException) {
	                
	                // Se a contagem de tentativas ainda não atingiu o limite...
	                if (user.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
	                	
	                    // ...apenas incrementamos a contagem de falhas.
	                    service.increaseFailedAttempt(user);
	                    exception = new BadCredentialsException("Usuário ou senha inválidos.");
	                } 
	                // Se atingiu o limite, bloqueamos a conta.
	                else {
	                    service.userAccountLock(user);
	                    exception = new LockedException(
	                            "Sua conta foi bloqueada por excesso de tentativas.");
	                }
	            }

	        } 
	        // 3. CASO: A CONTA JÁ ESTAVA BLOQUEADA
	        else if (!user.getAccountNonLocked()) {
	            // Verifica se o tempo de bloqueio já expirou
	            if (service.unlockAccountTimeExpired(user)) {
	                exception = new LockedException("Sua conta foi desbloqueada. Por favor, faça login novamente.");
	            } else {
	                exception = new LockedException("Sua conta está bloqueada. Tente novamente mais tarde.");
	            }
	        } 
	        // 4. CASO: A CONTA ESTÁ INATIVA (isEnable = false)
	        else {
	            exception = new LockedException("Sua conta está inativa. Verifique seu e-mail para ativá-la.");
	        }
	    }

	    // Finalmente, definimos a URL de falha e chamamos o método da superclasse
	    // para que ele coloque a 'exception' na sessão e redirecione o usuário.
	    // A sua página de login lerá a mensagem da exceção que definimos acima.
	    super.setDefaultFailureUrl("/signin?error=true"); // Usar ?error=true é uma boa prática
	    super.onAuthenticationFailure(request, response, exception);
	}
}