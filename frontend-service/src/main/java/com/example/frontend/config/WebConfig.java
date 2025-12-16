package com.example.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${backend.autostrada.url}")
    private String autostradaUrl;

    @Value("${backend.casello.url}")
    private String caselloUrl;

    @Value("${backend.corsia.url}")
    private String corsiaUrl;

    @Value("${backend.regione.url}")
    private String regioneUrl;

    @Value("${backend.dispositivi.url}")
    private String dispositiviUrl;

    @Value("${backend.biglietto.url}")
    private String bigliettoUrl;

    @Value("${backend.pagamento.url}")
    private String pagamentoUrl;

    @Value("${backend.multa.url}")
    private String multaUrl;

    @Value("${backend.veicolo.url}")
    private String veicoloUrl;

    @Value("${backend.utente.url}")
    private String utenteUrl;

    @Value("${backend.legacy.url:}")
    private String legacyUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }

    public String getAutostradaUrl() {
        return autostradaUrl;
    }

    public String getCaselloUrl() {
        return caselloUrl;
    }

    public String getCorsiaUrl() {
        return corsiaUrl;
    }

    public String getRegioneUrl() {
        return regioneUrl;
    }

    public String getDispositiviUrl() {
        return dispositiviUrl;
    }

    public String getBigliettoUrl() {
        return bigliettoUrl;
    }

    public String getPagamentoUrl() {
        return pagamentoUrl;
    }

    public String getMultaUrl() {
        return multaUrl;
    }

    public String getVeicoloUrl() {
        return veicoloUrl;
    }

    public String getUtenteUrl() {
        return utenteUrl;
    }

    public String getLegacyUrl() {
        return legacyUrl;
    }
}
