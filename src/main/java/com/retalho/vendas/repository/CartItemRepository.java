package com.retalho.vendas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.retalho.vendas.model.CartItem;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Para verificar se um produto específico já está dentro do carrinho de um usuário
    CartItem findByCartIdAndProdutoId(Long cartId, Long produtoId);

    // Para listar todos os itens de um carrinho específico
    List<CartItem> findByCartId(Long cartId);
    
    // Para contar quantos itens existem no carrinho do usuário
    Integer countByCartUserId(Long userId);
}