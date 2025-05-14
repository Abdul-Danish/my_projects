package com.change.streams;

import java.util.Map;

import org.bson.Document;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest.ChangeStreamRequestOptions;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Subscription;

import com.change.streams.model.Entity;
import com.change.streams.model.Event;
import com.change.streams.model.User;
import com.change.streams.service.UserService;
import com.change.streams.test.ClasssA;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableMongoAuditing
@Slf4j
public class SpringChangeStreamsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringChangeStreamsApplication.class, args);
	}

	@Autowired
	private UserService userService;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
    private ClasssA classA;
	
    @Override
    public void run(String... args) throws Exception {
//        startContainer();
//        System.out.println(temp());
        ClasssA classsA = new ClasssA();
        classsA.display();
    }
    
    private String temp() {
        return classA.display();
    }
    
    private void startContainer() {
        log.info("Starting Container");
        MessageListenerContainer container = new DefaultMessageListenerContainer(mongoTemplate);
        container.start();
        
        MessageListener<ChangeStreamDocument<Document>, Document> listener = message -> {
            try {
                System.out.println("Raw: " + message.getRaw());
                System.out.println("Body: " + message.getBody());
//                System.out.println(body);
    //            Entity.builder().;
            } catch (Exception e) {
                log.error("Exception Occured while Auditing: ", e);
            }
        };
        ChangeStreamRequestOptions options = new ChangeStreamRequestOptions("change_streams", "jv_snapshots", ChangeStreamOptions.empty());
        container.register(new ChangeStreamRequest<>(listener, options), Document.class);
    }

}
