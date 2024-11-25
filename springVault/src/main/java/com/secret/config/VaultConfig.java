package com.secret.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class VaultConfig {
//extends AbstractVaultConfiguration {

//    @Override
//    public VaultEndpoint vaultEndpoint() {
//        VaultEndpoint endpoint = null;
//        try {
//            endpoint = VaultEndpoint.from(new URI("http://localhost:8200"));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        return endpoint;
//    }
//
//    @Override
//    public ClientAuthentication clientAuthentication() {
//        return new TokenAuthentication("root");
////        return new TokenAuthentication("00000000-0000-0000-0000-000000000000");
//    }
//    
//    @Bean
//    public VaultTemplate vaultTemplate(VaultEndpoint vaultEndpoint, ClientAuthentication clientAuthentication) {
//        return new VaultTemplate(vaultEndpoint, clientAuthentication);
//    }
//    
//    @Bean
//    public VaultKeyValueOperations keyValueOperations(VaultTemplate vaultTemplate) {
//        return vaultTemplate.opsForKeyValue("transit/encrypt/mongo/", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
//    }
}