package com.example.sqslambda.configuration;

import com.example.sqslambda.converter.SuperHeroMessageConverter;
import com.example.sqslambda.entity.SuperHero;
import com.example.sqslambda.service.SuperHeroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@Slf4j
public class ApplicationConfiguration {

    @Bean
    public MessageConverter superHeroMessageConverter() {
        return new SuperHeroMessageConverter();
    }

    @Bean
    public Function<SuperHero, Mono<String>> handleEntity(final SuperHeroService superHeroService) {
        return superHero -> {
            log.debug("Imperative function received a super-hero message : {}", superHero);
            // Acknowledgement that the message is processed!
            return superHeroService.save(superHero);
        };
    }

}
