package com.qry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableSwagger2
@SpringBootApplication
public class QueryDslApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryDslApplication.class, args);
	}
	
//      @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2).select()
//                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).paths(PathSelectors.any())
//                .build().apiInfo(apiInfo()).useDefaultResponseMessages(false);
//    }
//    
//    @Bean
//    public ApiInfo apiInfo() {
//        final ApiInfoBuilder builder = new ApiInfoBuilder();
//        return builder.build();
//    }
}
