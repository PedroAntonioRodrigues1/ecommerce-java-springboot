package com.retalho.vendas.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.retalho.vendas.model.Produto;

public interface ProdutoService {

	public Produto salvarProduto(Produto p);
	
	public List<Produto> getAllProducts();
	
	public Boolean deleteProduct(Long id);
	
	public Produto getProductById (Long id);
	
	public Produto atualizarProduto(Produto produto,
            MultipartFile imagem1,
            MultipartFile imagem2,
            MultipartFile imagem3);
	
	public List<Produto> getAllActiveProducts();
	
	public List<Produto> searchProduct(String ch);
	
	public Page<Produto> getAllProdutoPagination(Integer pageNo,Integer pageSize);

	public Page<Produto> searchProdutosPagination(String searchTerm, Integer pageNo, Integer pageSize);

	public Page<Produto> getAllProdutoPaginationAdmin(Integer pageNo, Integer pageSize);

	
}
