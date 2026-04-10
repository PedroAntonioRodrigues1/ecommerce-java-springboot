package com.retalho.vendas.util;

public enum OrderStatus {
	
	IN_PROGRESS(1,"Em progresso..."),
	ORDER_RECIVED(2,"Pedido Recebido"),
	PRODUCT_PACKED(3,"Produto pronto para envio"),
	OUT_FOR_DELIVERY(4,"Saiu para entrega"),
	DELIVERED(5,"Entrega Realizada"),
	CANCELED(6,"Pedido Cancelado");
	
	private Integer id;
	
	private String name;

	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	

}
