package com.retalho.vendas.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAdress {

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nomeCompleto;
	private String cpf;
	private String email;
	private String telefone;
	// Endereço (preenchido via CEP)
    private String cep;
    private String endereco;     // logradouro
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    
    private Instant createdAt;
    private Instant updatedAt;
    
    private Boolean salvoNoPerfil;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDtls user;
}
