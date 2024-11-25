package com.secret.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultSysOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.VaultMount;

import com.secret.config.VaultConfig;
import com.secret.model.Credentials;

@Service
public class SecretsService {

//    @Autowired
//    private VaultTemplate vaultTemplate;

//    @Autowired
//    private VaultKeyValueOperations vaultKeyValueOperations;

//    @Autowired
//    private SecretsRepository secretsRepository;

    public SecretsService() {
//        System.out.println(vaultTemplate);
//        keyValueOperations = vaultTemplate.opsForKeyValue("spring-vault/secrets", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
    }

//    public String secureCredentials(Credentials credentials) {
//        vaultKeyValueOperations.put(credentials.getUsername(), credentials);
//        return "Done";
//    }
    
    /*
    public String encryptMessage(Credentials credentials) {
        
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        
//        VaultSysOperations sysOperations = vaultTemplate.opsForSys();
//        
//        if (!sysOperations.getMounts().containsKey("transit/")) {
//            sysOperations.mount("transit", VaultMount.create("transit"));
//            transitOperations.createKey("mongo");
//        }
        
        String cyphertext = transitOperations.encrypt("mongo", "a secret");
        System.out.println(cyphertext);
        System.out.println();
        
        String plaintext = transitOperations.decrypt("mongo", cyphertext);
        System.out.println(plaintext);
        
        return "ok";
    }
    */
    
    public Credentials saveCredentials(Credentials credentials) {
        return null;
//        return secretsRepository.save(credentials);
    }

    public Optional<Credentials> findById(String username) {
        return null;
//        return secretsRepository.findById(username);
    }
    
}
