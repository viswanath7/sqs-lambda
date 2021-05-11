package com.example.sqslambda;

import com.example.sqslambda.converter.SuperHeroMessageConverter;
import com.example.sqslambda.entity.SuperHero;
import com.example.sqslambda.service.SuperHeroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.messaging.converter.MessageConverter;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@SpringBootApplication
@EnableReactiveMongoRepositories
@Slf4j
public class ServerlessApplication {

    public static void main(String[] args) {
        FunctionalSpringApplication.run(ServerlessApplication.class, args);
    }

    @Autowired
    private SuperHeroService superHeroService;

    @Bean
    public MessageConverter superHeroMessageConverter() {
        return new SuperHeroMessageConverter();
    }

    @Bean
    public Function<SuperHero, Mono<String>> handleEntity() {
        return message -> {
            log.debug("Imperative function received a super-hero message : {}", message);
            // Acknowledgement that the message is processed!
            return superHeroService.save(message);
        };
    }

}
