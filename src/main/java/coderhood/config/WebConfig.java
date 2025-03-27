package coderhood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configura o CORS globalmente para permitir requisições de qualquer origem
        registry.addMapping("/**") // Aplica em todos os endpoints
                .allowedOrigins("http://localhost:3000")  // URL do seu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Métodos permitidos
                .allowedHeaders("*");  // Permite todos os cabeçalhos
    }
}