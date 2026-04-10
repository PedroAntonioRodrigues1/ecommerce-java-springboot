package com.retalho.vendas.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.retalho.vendas.model.Cart;
import com.retalho.vendas.model.CartItem;
import com.retalho.vendas.model.Produto;
import com.retalho.vendas.model.UserDtls;
import com.retalho.vendas.repository.CartItemRepository;
import com.retalho.vendas.repository.CartRepository;
import com.retalho.vendas.repository.ProdutoRepository;
import com.retalho.vendas.repository.UserRepository;
import com.retalho.vendas.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Override
    @Transactional
    public CartItem saveCart(Long productId, Long userId) {
        UserDtls user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Produto produto = produtoRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // 1. Busca o container (Cart) do usuário
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }

        // 2. Verifica se o produto já está lá dentro
        CartItem itemExistente = cartItemRepository.findByCartIdAndProdutoId(cart.getId(), productId);

        if (itemExistente != null) {
            return itemExistente;
        }

        // 3. Cria o novo item
        CartItem novoItem = new CartItem();
        novoItem.setCart(cart);
        novoItem.setProduto(produto);
        novoItem.setPriceAtAdd(produto.getPreco()); 

        return cartItemRepository.save(novoItem);
    }

    @Override
    public List<CartItem> getCartsbyUser(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            return new ArrayList<>();
        }
        return cart.getItems(); // Retorna a lista de CartItems dentro do Cart
    }

    @Override
    public Integer getCountCart(Long userId) {
        // Busca direto no repositório de itens por performance
        return cartItemRepository.countByCartUserId(userId);
    }

    @Override
    @Transactional
    public Boolean delectCartProduct(Long cartItemId) {
        if (cartItemRepository.existsById(cartItemId)) {
            cartItemRepository.deleteById(cartItemId);
            return true;
        }
        return false;
    }
}