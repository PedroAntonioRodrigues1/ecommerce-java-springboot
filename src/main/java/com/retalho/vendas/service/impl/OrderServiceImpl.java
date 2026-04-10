package com.retalho.vendas.service.impl;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.retalho.vendas.model.*;
import com.retalho.vendas.repository.*;
import com.retalho.vendas.service.OrderService;
import com.retalho.vendas.util.CommonUtil;
import com.retalho.vendas.util.OrderStatus;

import jakarta.mail.MessagingException;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository; 

	@Autowired
	private CommonUtil commonUtil;

	@Override
	@Transactional
	public void saveOrder(Long userid, OrderRequest orderRequest)
			throws UnsupportedEncodingException, MessagingException {

		UserDtls currentUser = userRepository.findById(userid)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

		// 1. Buscar Carrinho (Faltava essa linha no seu código)
		Cart cart = cartRepository.findByUserId(userid);
		if (cart == null || cart.getItems().isEmpty()) {
			throw new RuntimeException("Carrinho vazio");
		}

		// --- PASSO A: O ENDEREÇO DO PEDIDO (Histórico Imutável) ---
		OrderAdress orderAdress = new OrderAdress();
		copiarDadosEndereco(orderRequest, orderAdress); 
		orderAdress.setUser(currentUser);
		orderAdress.setCreatedAt(Instant.now());
		orderAdress.setUpdatedAt(Instant.now());
		orderAdress.setSalvoNoPerfil(Boolean.TRUE.equals(orderRequest.getSalvarEndereco()));

		// --- PASSO B: O ENDEREÇO DO PERFIL (CRUD / Reuso) ---
		if (Boolean.TRUE.equals(orderRequest.getSalvarEndereco())) {
			Endereco novoEndPerfil = new Endereco();
			
			novoEndPerfil.setCep(orderRequest.getCep());
			novoEndPerfil.setEndereco(orderRequest.getEndereco());
			novoEndPerfil.setNumero(orderRequest.getNumero());
			novoEndPerfil.setComplemento(orderRequest.getComplemento());
			novoEndPerfil.setBairro(orderRequest.getBairro());
			novoEndPerfil.setCidade(orderRequest.getCidade());
			novoEndPerfil.setEstado(orderRequest.getEstado());
			novoEndPerfil.setUser(currentUser);
			
			enderecoRepository.save(novoEndPerfil);
		}

		// --- PASSO C: FINALIZAR O PEDIDO ---
		Order mainOrder = new Order();
		mainOrder.setOrderId(UUID.randomUUID().toString());
		mainOrder.setOrderDate(Instant.now());
		mainOrder.setStatus(OrderStatus.IN_PROGRESS.getName());
		mainOrder.setPaymentType(orderRequest.getPaymentType());
		mainOrder.setUser(currentUser);
		mainOrder.setAdress(orderAdress);

		// 4. Criar os itens (ProdutoOrder) vinculados à sacola
		List<ProdutoOrder> orderItems = new ArrayList<>();
		for (CartItem item : cart.getItems()) {
			ProdutoOrder pOrder = new ProdutoOrder();
			pOrder.setOrder(mainOrder); 
			pOrder.setProduto(item.getProduto());
			pOrder.setPriceSnapshot(item.getPriceAtAdd()); 
			orderItems.add(pOrder);
		}

		mainOrder.setItems(orderItems);

		// 5. Salvar Pedido (Cascade salvará o orderAdress e os orderItems)
		Order savedOrder = orderRepository.save(mainOrder);
		
		try {
			commonUtil.sendMailForOrder(savedOrder, savedOrder.getStatus());
		} catch (Exception e) {
			e.printStackTrace(); 
		}

		// 6. Limpar carrinho
		cartItemRepository.deleteAll(cart.getItems());
	}

	// --- MÉTODO AUXILIAR QUE FALTAVA ---
	private void copiarDadosEndereco(OrderRequest de, OrderAdress para) {
		para.setNomeCompleto(de.getNomeCompleto());
		para.setCpf(de.getCpf());
		para.setEmail(de.getEmail());
		para.setTelefone(de.getTelefone());
		para.setCep(de.getCep());
		para.setEndereco(de.getEndereco());
		para.setNumero(de.getNumero());
		para.setComplemento(de.getComplemento());
		para.setBairro(de.getBairro());
		para.setCidade(de.getCidade());
		para.setEstado(de.getEstado());
	}

	@Override
	public List<Order> getOrdersbyUser(Long userId) {
		return orderRepository.findByUserId(userId);
	}

	@Override
	public Order updateOrderStatus(Long id, String status) {
		Order order = orderRepository.findById(id).orElse(null);
		if (order != null) {
			order.setStatus(status);
			return orderRepository.save(order);
		}
		return null;
	}

	@Override
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Order getOrderByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId);
	}

	@Override
	public Page<Order> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findAll(pageable);
	}

	@Override
	public Page<Order> searchOrdersPagination(String term, Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findByOrderIdContainingIgnoreCase(term, pageable);
	}
}