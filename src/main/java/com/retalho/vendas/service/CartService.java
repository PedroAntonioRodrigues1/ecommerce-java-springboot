package com.retalho.vendas.service;

import java.util.List;
import com.retalho.vendas.model.CartItem; // <--- Importante: Importar CartItem

public interface CartService {

    // Retorna CartItem, porque você está adicionando um item à sacola
    public CartItem saveCart(Long pid, Long uid);
    
    // Retorna uma lista de Itens (os produtos que o cara escolheu)
    public List<CartItem> getCartsbyUser(Long userId);
    
    // Quantidade de itens na sacola
    public Integer getCountCart(Long userId);
    
    // Deleta um item específico pelo ID do CartItem
    public Boolean delectCartProduct(Long cartItemId);
}