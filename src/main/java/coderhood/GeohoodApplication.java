package coderhood;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeohoodApplication {
    public static void main(String[] args) {
        // Carrega variáveis do .env
        Dotenv dotenv = Dotenv.configure()
                .filename(".env") // usa o .env padrão
                .ignoreIfMissing() // não dá erro se o arquivo não existir
                .load();

        // Define cada variável do .env como propriedade do sistema
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(GeohoodApplication.class, args);
    }
}
