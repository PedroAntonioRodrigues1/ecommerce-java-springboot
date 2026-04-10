package com.retalho.vendas.config;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CONFIGURAÇÃO DE ARQUIVOS ESTÁTICOS E IMAGENS EXTERNAS
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Garante que o Spring encontre seu CSS e JS dentro de src/main/resources/static
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        // 2. Cria o "túnel" /img/ para buscar as fotos no seu Disco C:
        // IMPORTANTE: O caminho deve terminar com "/"
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///C:/conexao-retalhos/imagens/");
    }

    /**
     * Define o resolvedor de Locale (Idioma)
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("pt", "BR"));
        return slr;
    }

    /**
     * Configura o MessageSource (Textos/Mensagens)
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}