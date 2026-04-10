package com.retalho.vendas.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import org.springframework.data.domain.Page;
import com.retalho.vendas.model.Order;
import com.retalho.vendas.model.OrderRequest;
import jakarta.mail.MessagingException;

public interface OrderService {
    
    public void saveOrder(Long userid, OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException;
    
    public List<Order> getOrdersbyUser(Long userId);
    
    public Order updateOrderStatus(Long id, String status);
    
    public List<Order> getAllOrders();
    
    public Order getOrderByOrderId(String orderId);

    public Page<Order> getAllOrdersPagination(Integer pageNo, Integer pageSize);
    
    public Page<Order> searchOrdersPagination(String trim, Integer pageNo, Integer pageSize);
}