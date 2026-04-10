package com.retalho.vendas.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador legível/externo (UUID ou sequência formatada)
     */
    @Column(nullable = false, unique = true)
    private String orderId;

    private Instant orderDate;
    private String status;
    private String paymentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserDtls user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Adicione o cascade aqui!
    @JoinColumn(name = "adress_id")
    private OrderAdress adress;
    /**
     * Itens do pedido: cascade do Order para OrderItem é apropriado.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProdutoOrder> items = new ArrayList<>();
    private BigDecimal shippingCost = BigDecimal.ZERO; // Começa como zero (Frete Grátis)

    // Método para o valor dos PRODUTOS
    public BigDecimal getSubtotal() {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(ProdutoOrder::getPriceSnapshot)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getShippingCost() {
        return shippingCost != null ? shippingCost : BigDecimal.ZERO;
    }
    
 // Adicione isso no final da sua classe Order
    public BigDecimal getTotalOrderPrice() {
        // Soma o Subtotal (produtos) com o Custo de Frete
        return getSubtotal().add(getShippingCost());
    }
}
