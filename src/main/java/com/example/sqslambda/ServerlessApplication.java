package com.example.sqslambda;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.util.function.Function;

@SpringBootApplication
@Slf4j
public class ServerlessApplication {

    public static void main(String[] args) {
        FunctionalSpringApplication.run(ServerlessApplication.class, args);
    }

    @Bean
    public Function<Message<String>, Boolean> imperativeMessageFunction() {
        return message -> {
            log.debug("Imperative function received a message from the queue: {}", message);
            // Acknowledgement that the message is processed!
            return true;
        };
    }

}
