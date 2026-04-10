package com.retalho.vendas.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.retalho.vendas.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Busca todos os pedidos de um usuário
    List<Order> findByUserId(Long userId);
    
    // Busca um pedido pelo código UUID (String)
    Order findByOrderId(String orderId);

    // Paginação para o Admin
    Page<Order> findAll(Pageable pageable);
    
    // Busca por ID na barra de pesquisa do Admin
    Page<Order> findByOrderIdContainingIgnoreCase(String orderId, Pageable pageable);
}