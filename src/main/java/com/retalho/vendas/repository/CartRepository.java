package com.retalho.vendas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.retalho.vendas.model.Cart;
import java.util.Optional;

// Mudamos de Integer para Long no ID
public interface CartRepository extends JpaRepository<Cart, Long> {
	
    // Agora o Cart é 1 para 1 com o usuário. 
    // Buscamos o carrinho "container" do usuário.
    Cart findByUserId(Long userId);

    // O método antigo countByuserDtlsId não faz mais sentido aqui, 
    // pois o Cart agora é o container. O count de itens será feito no CartItem ou via Java.
}