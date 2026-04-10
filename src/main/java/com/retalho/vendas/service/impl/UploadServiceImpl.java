package com.retalho.vendas.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.retalho.vendas.service.UploadService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class UploadServiceImpl implements UploadService {

    // Defina um caminho absoluto fora do projeto
    private final String DIRETORIO_PAI = "C:/conexao-retalhos/imagens"; 

    @Override
    public String salvarImagem(MultipartFile arquivo, String subPasta) throws IOException {
        if (arquivo.isEmpty()) return null;

        Path uploadPath = Paths.get(DIRETORIO_PAI + File.separator + subPasta);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extensao = FilenameUtils.getExtension(arquivo.getOriginalFilename());
        String nomeUnico = "prod_" + System.currentTimeMillis() + "." + extensao;

        Path path = uploadPath.resolve(nomeUnico);
        Files.copy(arquivo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return nomeUnico;
    }
}
