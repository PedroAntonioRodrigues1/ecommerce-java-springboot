package com.retalho.vendas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.retalho.vendas.util.ProdutoInfo;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Produto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 500)
	private String titulo;
	
	@Column(length = 5000)
	private String descricao;
	
	private BigDecimal preco;
	
	@Enumerated(EnumType.STRING)
    private ProdutoInfo info;
	
	
    private Boolean ativo;
    
    @ElementCollection
    private List<String> imagens = new ArrayList<>();
    
    @Version
    private Long version;

}
