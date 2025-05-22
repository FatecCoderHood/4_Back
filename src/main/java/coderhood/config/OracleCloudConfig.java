package coderhood.config;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AbstractAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class OracleCloudConfig {

    @Value("${oracle.cloud.tenancyId}")
    private String tenancyId;

    @Value("${oracle.cloud.userId}")
    private String userId;

    @Value("${oracle.cloud.fingerprint}")
    private String fingerprint;

    @Value("${oracle.cloud.privateKey}")
    private String privateKey;

    @Value("${oracle.cloud.regionId}")
    private String regionId;

    @Bean
    public AbstractAuthenticationDetailsProvider authenticationDetailsProvider() {
        // Garante que a chave esteja no formato PEM correto
        String pemKey = formatAsPem(privateKey);

        return SimpleAuthenticationDetailsProvider.builder()
                .tenantId(tenancyId)
                .userId(userId)
                .fingerprint(fingerprint)
                .privateKeySupplier(() -> new ByteArrayInputStream(pemKey.getBytes(StandardCharsets.UTF_8)))
                .region(Region.fromRegionId(regionId))
                .build();
    }

    private String formatAsPem(String keyContent) {
        // Remove possíveis escapes existentes
        String cleanedKey = keyContent.replace("\\n", "\n");

        // Se já estiver no formato PEM, retorna como está
        if (cleanedKey.contains("-----BEGIN PRIVATE KEY-----")) {
            return cleanedKey;
        }

        // Formata corretamente como PEM
        return "-----BEGIN PRIVATE KEY-----\n" +
                cleanedKey +
                "\n-----END PRIVATE KEY-----";
    }

    @Bean
    public ObjectStorage objectStorage() {
        return ObjectStorageClient.builder()
                .build(authenticationDetailsProvider());
    }
}
