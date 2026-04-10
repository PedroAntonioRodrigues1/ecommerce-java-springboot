package com.retalho.vendas.repository;

import com.retalho.vendas.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByUserId(Long userId);
}