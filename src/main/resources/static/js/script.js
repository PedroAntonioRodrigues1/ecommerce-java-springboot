$(function() {

	// ==========================================
	// MÉTODOS PERSONALIZADOS (Custom Methods)
	// ==========================================

	// 1. Nome sem números
	$.validator.addMethod("nomeValido", function(value, element) {
		return this.optional(element) || /^[A-Za-zÀ-ÿ\s]+$/.test(value);
	}, "O nome não pode conter números ou símbolos.");

	// 2. Telefone válido (Aceita com ou sem máscara)
	$.validator.addMethod("telefoneValido", function(value, element) {
		return this.optional(element) || /^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$/.test(value);
	}, "Digite um telefone válido com DDD.");

	// 3. Sem espaços em branco (Ideal para senhas e logins)
	$.validator.addMethod("semEspaco", function(value, element) {
		return value.indexOf(" ") < 0 && value != ""; 
	}, "Este campo não pode conter espaços em branco.");

	// 4. Senha Forte (Mínimo 8 chars, 1 maiúscula, 1 minúscula, 1 número, 1 caractere especial)
	$.validator.addMethod("senhaForte", function(value, element) {
		return this.optional(element) || /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(value);
	}, "A senha deve conter letras (maiúsculas e minúsculas), números e um caractere especial.");

	// 5. Validação Real de CPF (Cálculo matemático)
	$.validator.addMethod("cpfValido", function(value, element) {
		value = value.replace(/[^\d]+/g, ''); // Remove pontuação
		if (value == '') return this.optional(element) || false;
		if (value.length != 11 || /^(.)\1+$/.test(value)) return false; // Bloqueia 111.111.111-11
		
		let add = 0, rev = 0;
		for (let i = 0; i < 9; i ++) add += parseInt(value.charAt(i)) * (10 - i);
		rev = 11 - (add % 11);
		if (rev == 10 || rev == 11) rev = 0;
		if (rev != parseInt(value.charAt(9))) return false;
		
		add = 0;
		for (let i = 0; i < 10; i ++) add += parseInt(value.charAt(i)) * (11 - i);
		rev = 11 - (add % 11);
		if (rev == 10 || rev == 11) rev = 0;
		if (rev != parseInt(value.charAt(10))) return false;
		return true;
	}, "Digite um CPF válido.");

	// 6. CEP Válido
	$.validator.addMethod("cepValido", function(value, element) {
		return this.optional(element) || /^[0-9]{5}-?[0-9]{3}$/.test(value);
	}, "Digite um CEP válido.");


	// ==========================================
	// CONFIGURAÇÕES GERAIS (Reutilizáveis)
	// ==========================================
	const defaultHighlight = function(element) {
		$(element).addClass("is-invalid").removeClass("is-valid");
	};
	const defaultUnhighlight = function(element) {
		$(element).removeClass("is-invalid").addClass("is-valid");
	};
	const defaultErrorPlacement = function(error, element) {
		error.addClass("invalid-feedback d-block");
		if (element.closest('.input-group').length) {
			error.insertAfter(element.closest('.input-group'));
		} else if (element.is(":radio") || element.is(":checkbox")) {
			error.appendTo(element.parent());
		} else {
			error.insertAfter(element);
		}
	};


	// ==========================================
	// Validação: Cadastro de Usuário
	// ==========================================
	$("#cadastroUser").validate({
		rules: {
			nome: { 
				required: true, 
				minlength: 3, 
				nomeValido: true,
				normalizer: function(value) { return $.trim(value); } // Remove espaços falsos
			},
			loginEmail: { 
				required: true, 
				email: true, 
				semEspaco: true,
				normalizer: function(value) { return $.trim(value); }
			},
			telefone: { required: true, telefoneValido: true },
			senha: { 
				required: true, 
				minlength: 3, 
				semEspaco: true, 
				senhaForte: false 
			},
			confirmarSenha: { required: true, equalTo: "#senha" }
		},
		messages: {
			nome: { required: "Digite seu nome completo", minlength: "O nome deve ter pelo menos 3 caracteres" },
			loginEmail: { required: "Informe seu e-mail", email: "Digite um e-mail válido" },
			telefone: { required: "Informe seu telefone" },
			senha: { required: "Digite uma senha", minlength: "A senha deve ter pelo menos 8 caracteres" },
			confirmarSenha: { required: "Repita a senha", equalTo: "As senhas não coincidem" }
		},
		errorClass: "is-invalid",
		validClass: "is-valid",
		errorPlacement: defaultErrorPlacement,
		highlight: defaultHighlight,
		unhighlight: defaultUnhighlight
	});

	// ==========================================
	// Validação: Recuperar Senha
	// ==========================================
	$("#resetPassword").validate({
		rules: {
			password: { required: true, minlength: 8, semEspaco: true, senhaForte: true },
			confirmPassword: { required: true, equalTo: "#password" }
		},
		messages: {
			password: { required: "Digite a nova senha", minlength: "A senha deve ter pelo menos 8 caracteres" },
			confirmPassword: { required: "Confirme a nova senha", equalTo: "As senhas não coincidem" }
		},
		errorClass: "is-invalid",
		validClass: "is-valid",
		errorPlacement: defaultErrorPlacement,
		highlight: defaultHighlight,
		unhighlight: defaultUnhighlight
	});

	// ==========================================
	// Validação: Formulário de Pedido (Stepper)
	// ==========================================
	$("#order-form").validate({
		ignore: ":hidden",
		errorElement: "div",
		rules: {
			nomeCompleto: { 
				required: true, 
				minlength: 3, 
				nomeValido: true,
				normalizer: function(value) { return $.trim(value); }
			},
			cpf: { required: true, cpfValido: true }, // Trocado minlength pelo algoritmo real
			telefone: { required: true, telefoneValido: true },
			email: { 
				required: true, 
				email: true, 
				semEspaco: true,
				normalizer: function(value) { return $.trim(value); }
			},
			cep: { required: true, cepValido: true },
			endereco: { required: true },
			numero: { required: true },
			bairro: { required: true },
			cidade: { required: true },
			estado: { required: true },
			paymentType: { required: true }
		},
		messages: {
			nomeCompleto: { required: "Informe seu nome completo", minlength: "O nome deve ter pelo menos 3 caracteres" },
			cpf: { required: "Informe seu CPF" }, // A msg de inválido já vem do método
			telefone: { required: "Informe seu telefone" },
			email: { required: "Informe seu e-mail", email: "Digite um e-mail válido" },
			cep: { required: "Informe o CEP" },
			endereco: { required: "Informe o endereço" },
			numero: { required: "Informe o número" },
			bairro: { required: "Informe o bairro" },
			cidade: { required: "Informe a cidade" },
			estado: { required: "Selecione um estado" },
			paymentType: { required: "Selecione a forma de pagamento" }
		},
		errorClass: "is-invalid",
		validClass: "is-valid",
		errorPlacement: defaultErrorPlacement,
		highlight: defaultHighlight,
		unhighlight: defaultUnhighlight
	});
	// ==========================================
		// Validação: Perfil - Dados Pessoais
		// ==========================================
		$("#form-dados").validate({
			rules: {
				nome: { required: true, minlength: 3, nomeValido: true },
				telefone: { required: true, telefoneValido: true }
			},
			messages: {
				nome: { required: "O nome é obrigatório" },
				telefone: { required: "Informe um telefone válido" }
			},
			errorClass: "is-invalid",
			validClass: "is-valid",
			errorPlacement: defaultErrorPlacement,
			highlight: defaultHighlight,
			unhighlight: defaultUnhighlight
		});

		// ==========================================
		// Validação: Perfil - Alterar Senha
		// ==========================================
		$("#form-senha").validate({
			rules: {
				currentPassword: { required: true },
				newPassword: { required: true, minlength: 8, semEspaco: true, senhaForte: true },
				confirmaSenha: { required: true, equalTo: "#newPassword" }
			},
			messages: {
				currentPassword: { required: "Digite sua senha atual" },
				newPassword: { required: "Digite a nova senha", minlength: "Mínimo 8 caracteres" },
				confirmaSenha: { required: "Confirme a senha", equalTo: "As senhas não coincidem" }
			},
			errorClass: "is-invalid",
			validClass: "is-valid",
			errorPlacement: defaultErrorPlacement,
			highlight: defaultHighlight,
			unhighlight: defaultUnhighlight
		});

		// ==========================================
		// Validação: Perfil - Endereços (Novo e Editar)
		// ==========================================
		$(".form-endereco").each(function() {
			$(this).validate({
				rules: {
					nomeCompleto: { required: true, minlength: 3, nomeValido: true },
					telefone: { required: true, telefoneValido: true },
					cep: { required: true, cepValido: true },
					endereco: { required: true },
					numero: { required: true },
					bairro: { required: true },
					cidade: { required: true },
					estado: { required: true }
				},
				errorClass: "is-invalid",
				validClass: "is-valid",
				errorPlacement: defaultErrorPlacement,
				highlight: defaultHighlight,
				unhighlight: defaultUnhighlight
			});
		});

});