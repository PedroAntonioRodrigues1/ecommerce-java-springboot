package com.retalho.vendas.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.retalho.vendas.model.Produto; 

@Service
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	
	List<Produto> findByAtivoTrue();
	
	List<Produto> findByTituloContainingIgnoreCase(String ch);
	
	Page<Produto> findByAtivoTrue(Pageable pageable);

	// No ProdutoRepository
	Page<Produto> findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
	    String titulo, String descricao, Pageable pageable);
	
}
