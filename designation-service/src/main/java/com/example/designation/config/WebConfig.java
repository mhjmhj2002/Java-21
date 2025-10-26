package com.example.designation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica a configuração para todos os endpoints sob /api/
                .allowedOrigins("*") // Permite requisições de qualquer origem. Para produção, restrinja a `http://seu-dominio.com`
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS" ) // Métodos HTTP permitidos
                .allowedHeaders("*"); // Permite todos os cabeçalhos
    }
}
