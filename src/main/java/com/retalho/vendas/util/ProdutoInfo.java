package com.retalho.vendas.util;

public enum ProdutoInfo {
	// Agora passamos apenas os 3 campos: Material, Cuidados e Dimensões
    COLCHA("Algodão e Fibra Tricoline", "Lavar à mão, não usar alvejante", "2,20 x 2,40 m"),
    BOLSA("Tecido Jeans e Retalhos de Chita", "Limpar com pano úmido", "30cm x 40cm"),
    TAPETE("Malha de Algodão Reciclada", "Pode ser lavado na máquina (ciclo leve)", "50cm x 80cm"),
    CAMINHO_DE_MESA("Linho com Aplicações", "Passar a ferro em temperatura média", "1,50m x 0,40m");

    private final String material;
    private final String cuidados;
    private final String dimensoes;

    // O construtor deve ter o mesmo nome da Enum (ProdutoInfo)
    private ProdutoInfo(String material, String cuidados, String dimensoes) {
        this.material = material;
        this.cuidados = cuidados;
        this.dimensoes = dimensoes;
    }

    // Getters focados no que você precisa
    public String getMaterial() { 
        return material; 
    }

    public String getCuidados() { 
        return cuidados; 
    }

    public String getDimensoes() { 
        return dimensoes; 
    }
}
