package com.retalho.vendas.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.retalho.vendas.model.Produto;
import com.retalho.vendas.repository.ProdutoRepository;
import com.retalho.vendas.service.ProdutoService;

@Service
public class ProdutoServiceImpl implements ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	
	
	@Override
	public Produto salvarProduto(Produto p) {
		
		return produtoRepository.save(p);
	}

	
	@Override
	public List<Produto> getAllProducts() {
		return produtoRepository.findAll();
		
		
	}


	@Override
	public Boolean deleteProduct(Long id) {
		Produto produto = produtoRepository.findById(id).orElse(null);
		if(!ObjectUtils.isEmpty(produto)) {
			produtoRepository.delete(produto);
			return true;
		}
		return false;
	}


	@Override
	public Produto getProductById(Long id) {
		Produto produto = produtoRepository.findById(id).orElse(null);
		return produto;
	}


	@Override
    public Produto atualizarProduto(Produto produto, 
                                    MultipartFile imagem1, 
                                    MultipartFile imagem2, 
                                    MultipartFile imagem3) {
        Produto existente = getProductById(produto.getId());
        if (existente == null) {
            return null;
        }

        // Atualiza campos básicos
        existente.setTitulo(produto.getTitulo());
        existente.setPreco(produto.getPreco());
        existente.setDescricao(produto.getDescricao());
        existente.setAtivo(produto.getAtivo());
        

        // Preserva imagens antigas se não receber novas
        List<String> imagens = existente.getImagens() != null ? new ArrayList<>(existente.getImagens()) : new ArrayList<>();
        MultipartFile[] novas = { imagem1, imagem2, imagem3 };

        for (int i = 0; i < novas.length; i++) {
            MultipartFile novaImg = novas[i];
            if (novaImg != null && !novaImg.isEmpty()) {
                try {
                    String nomeArquivo = salvarArquivoImagem(novaImg, i);
                    if (imagens.size() > i) {
                        imagens.set(i, nomeArquivo); // substitui a antiga na posição
                    } else {
                        imagens.add(nomeArquivo); // adiciona se não existir
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        existente.setImagens(imagens);

        return produtoRepository.save(existente);
    }

    private String salvarArquivoImagem(MultipartFile arquivo, int indice) throws IOException {
        String extensao = FilenameUtils.getExtension(arquivo.getOriginalFilename());
        String nomeUnico = "produto_" + System.currentTimeMillis() + "_" + indice + "." + extensao;

        File pastaBase = new ClassPathResource("static/img/produto_img").getFile();
        if (!pastaBase.exists()) {
            pastaBase.mkdirs();
        }

        Path destino = Paths.get(pastaBase.getAbsolutePath(), nomeUnico);
        Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nomeUnico;
    }


	@Override
	public List<Produto> getAllActiveProducts() {
		List<Produto> produto = produtoRepository.findByAtivoTrue();
		return produto;
	}


	@Override
	public List<Produto> searchProduct(String ch) {
		List<Produto> produtoSearch = produtoRepository.findByTituloContainingIgnoreCase(ch);
		
		return produtoSearch;
    
   
	}


	@Override
	public Page<Produto> getAllProdutoPagination(Integer pageNo, Integer pageSize) {
	 Pageable pageable = PageRequest.of(pageNo, pageSize);
	 Page<Produto> pageProduct = produtoRepository.findByAtivoTrue(pageable);
		return pageProduct;
	}
	
	@Override
	public Page<Produto> getAllProdutoPaginationAdmin(Integer pageNo, Integer pageSize) {
	    Pageable pageable = PageRequest.of(pageNo, pageSize);
	    // Busca todos os produtos (ativos e inativos) para admin
	    return produtoRepository.findAll(pageable);
	}

	@Override
	public Page<Produto> searchProdutosPagination(String searchTerm, Integer pageNo, Integer pageSize) {
	    Pageable pageable = PageRequest.of(pageNo, pageSize);
	    return produtoRepository.findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
	        searchTerm, searchTerm, pageable);
	}
	
}
 