package com.retalho.vendas.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ProdutoOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Relacionamento com o pedido (pai)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	/**
	 * Produto referenciado. Sem cascade: produto é entidade independente.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "produto_id")
	private Produto produto;

	/**
	 * Snapshot do preço no momento da compra.
	 */
	private BigDecimal priceSnapshot;

	/**
	 * Como cada produto é único, quantity pode ser omitido ou fixo em 1. Se no
	 * futuro permitir múltiplas unidades, adicione quantity.
	 */
}
