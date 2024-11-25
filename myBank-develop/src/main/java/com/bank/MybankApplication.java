package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
//@EnableSwagger2
@EnableMongoAuditing
@EnableMongoRepositories("com.bank.repository.mongo")
@EnableJpaRepositories("com.bank.repository.jpa")
public class MybankApplication {

//	@Bean
//	public Docket api() {
//		return new Docket(DocumentationType.SWAGGER_2).select()
//				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).paths(PathSelectors.any())
//				.build().apiInfo(apiInfo()).useDefaultResponseMessages(false);
//	}
//
//	@Bean
//	public ApiInfo apiInfo() {
//		final ApiInfoBuilder builder = new ApiInfoBuilder();
//		return builder.build();
//	}
	

	public static void main(String[] args) {
		SpringApplication.run(MybankApplication.class, args);
	}

}
