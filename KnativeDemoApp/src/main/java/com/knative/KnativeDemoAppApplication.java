package com.knative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class KnativeDemoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnativeDemoAppApplication.class, args);
    }

    @RestController
    class HelloworldController {
        @GetMapping("/")
        String hello(@RequestParam("timedelay") long timeDelay) throws InterruptedException {
            log.info("Time Delay: {}", timeDelay);
            log.info("Thread Sleeping...");
            Thread.sleep(timeDelay);
            log.info("Thread Resumed...");
            return "Hello ! what's up";
        }
    }

}
