package com.example.sqslambda.configuration;

import com.example.sqslambda.converter.SuperHeroMessageConverter;
import com.example.sqslambda.entity.SuperHero;
import com.example.sqslambda.service.SuperHeroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Configuration
@Slf4j
public class ApplicationConfiguration {

    @Bean
    public MessageConverter superHeroMessageConverter() {
        return new SuperHeroMessageConverter();
    }

    @Bean
    public Function<SuperHero, String> handleEntity(final SuperHeroService superHeroService) {
        return superHero -> {
            log.debug("Imperative function received a super-hero message : {}", superHero);
            // Acknowledgement that the message is processed!
            try {
                return superHeroService.save(superHero)
                        .doOnSuccess(identifier -> log.debug("Successfully persisted superhero with identifier {}", identifier))
                        .toFuture()
                        .get(10, TimeUnit.SECONDS);
            } catch (InterruptedException|ExecutionException|TimeoutException ex) {
                log.error(String.format("Failed to save super hero entity %s", superHero), ex);
                return "";
            }
        };
    }

}
