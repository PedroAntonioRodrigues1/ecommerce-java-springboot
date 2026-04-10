package com.retalho.vendas.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.retalho.vendas.model.ProdutoOrder;

public interface ProdutoOrderRepository extends JpaRepository<ProdutoOrder, Long> {

    // Se você precisar listar os itens de uma sacola específica:
    List<ProdutoOrder> findByOrderOrderId(String orderId);

    Page<ProdutoOrder> findAll(Pageable pageable);

    Optional<ProdutoOrder> findById(Long id);
}