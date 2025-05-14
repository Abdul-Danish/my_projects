package com.change.streams.config;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.change.streams.model.Address;
import com.change.streams.model.User;
import com.change.streams.repository.UserRepository;

@Configuration
public class JaversConfig {

//    @Bean("javersBean")
//    Javers javersBean() {
////        return JaversBuilder.javers().registerEntities(User.class, Address.class).build();
//        JaversBuilder.javers().registerValueTypeAdapter(null);
//    }
    
}
