package coderhood.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public com.fasterxml.jackson.databind.Module uuidModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(UUID.class, new ToStringSerializer());
        return module;
    }
}
