package com.retalho.vendas.config;



// Importações de classes do Spring e do Spring Security
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


// A anotação @Configuration indica que esta classe é uma fonte de definições de beans para o contêiner Spring.
@Configuration
public class SecurityConfig {

	// A anotação @Autowired injeta automaticamente uma implementação de AuthenticationSuccessHandler.
	// Este handler define o que acontece após um login bem-sucedido (ex: redirecionar com base no perfil).
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Autowired
	@Lazy
	private AuthFailureHandlerImpl authenticationFailureHandler;

	/**
	 * Define um bean para o codificador de senhas.
	 * O Spring Security exige um PasswordEncoder para comparar senhas de forma segura.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		// Retorna uma instância do BCryptPasswordEncoder, um algoritmo forte e amplamente recomendado
		// para hashing de senhas. Ele armazena um "sal" junto com o hash para maior segurança.
		return new BCryptPasswordEncoder();
	}

	/**
	 * Define um bean para o serviço de detalhes do usuário (UserDetailsService).
	 * Este serviço é a ponte entre o Spring Security e os dados dos seus usuários (ex: banco de dados).
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		// Retorna uma instância da sua implementação customizada (UserDetailsServiceImpl).
		// É nesta classe que você implementa a lógica para buscar um usuário pelo nome de usuário.
		return new UserDetailsServiceImpl();
	}

	/**
	 * Configura e registra um DaoAuthenticationProvider no Spring Security.
	 * 
	 * O DaoAuthenticationProvider é responsável por autenticar usuários usando
	 * dados vindos de um banco de dados (via UserDetailsService) e validar a senha
	 * usando um PasswordEncoder.
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		// Cria uma instância do provedor de autenticação baseado em DAO
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

		// Define o serviço que vai buscar o usuário no banco de dados.
		// O provider usará este serviço para carregar os detalhes do usuário durante a tentativa de login.
		authenticationProvider.setUserDetailsService(userDetailsService());

		// Define o codificador de senha (BCrypt) para validar as credenciais.
		// O provider usará este encoder para comparar a senha fornecida no login com a senha armazenada (hash).
		authenticationProvider.setPasswordEncoder(passwordEncoder());

		// Retorna o provedor configurado para o Spring Security usar
		return authenticationProvider;
	}

	/**
	 * Define a cadeia de filtros de segurança principal da aplicação.
	 * É aqui que a maior parte da configuração de segurança web é definida.
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// O objeto http permite construir e customizar a segurança.
		http
				// Desativa a proteção contra CSRF (Cross-Site Request Forgery).
				// ATENÇÃO: Em aplicações web tradicionais, isso é um risco de segurança.
				// É comum desativar para APIs RESTful ou se você tiver outra forma de proteção.
				.csrf(csrf -> csrf.disable())

				// Desativa a configuração de CORS (Cross-Origin Resource Sharing).
				.cors(cors -> cors.disable())

				// Inicia a configuração de regras de autorização para as requisições HTTP.
				.authorizeHttpRequests(req -> req
						// Define que qualquer URL sob "/user/**" exige que o usuário tenha a "ROLE_USER".
						// O Spring Security adiciona o prefixo "ROLE_" automaticamente.
						.requestMatchers("/user/**").hasRole("USER")

						// Define que qualquer URL sob "/admin/**" exige que o usuário tenha a "ROLE_ADMIN".
						.requestMatchers("/admin/**").hasRole("ADMIN")

						// Define que todas as outras URLs ("/**") são permitidas para qualquer um (permitAll).
						// A ordem das regras é importante; as mais específicas devem vir primeiro.
						.requestMatchers("/**").permitAll())

				// Inicia a configuração do processo de login via formulário HTML.
				.formLogin(form -> form
						// Especifica a URL da sua página de login customizada.
						.loginPage("/signin")

						// Especifica a URL que o formulário de login deve enviar (POST) para ser processada pelo Spring.
						.loginProcessingUrl("/login")
						
						// Define nosso handler customizado para ser executado quando o login FALHAR.
						.failureHandler(authenticationFailureHandler)

						// Define um handler customizado para ser executado após um login bem-sucedido.
						.successHandler(authenticationSuccessHandler)

						
				)
				// Inicia a configuração do processo de logout.
				.logout(logout -> logout.permitAll()); // Permite que qualquer usuário acesse a URL de logout.

		// Constrói e retorna o objeto SecurityFilterChain configurado.
		return http.build();
	}
}