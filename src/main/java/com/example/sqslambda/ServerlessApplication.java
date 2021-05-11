package com.example.sqslambda;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = {"com.example.sqslambda"})
@EnableReactiveMongoRepositories
@Slf4j
public class ServerlessApplication {

    public static void main(String[] args) {
        FunctionalSpringApplication.run(ServerlessApplication.class, args);
    }

}
