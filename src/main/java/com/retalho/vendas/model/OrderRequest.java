package com.retalho.vendas.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OrderRequest {
	
	
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
    
    
    private Boolean salvarEndereco;
    private String paymentType;

}
