package com.retalho.vendas.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface UploadService {
    // Retorna o nome do arquivo salvo ou a URL dele
    String salvarImagem(MultipartFile arquivo, String subPasta) throws IOException;
}