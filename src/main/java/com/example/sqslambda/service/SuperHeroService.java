package com.example.sqslambda.service;

import com.example.sqslambda.entity.SuperHero;
import com.example.sqslambda.repository.SuperHeroRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class SuperHeroService {

    private final SuperHeroRepository superHeroRepository;

    public SuperHeroService(final SuperHeroRepository superHeroRepository) {
        this.superHeroRepository = superHeroRepository;
    }

    /**
     * Saves the supplied entity into the data store
     * @param superHero Entity to save
     * @return identifier of the saved entity
     */
    public Mono<String> save(final SuperHero superHero) {
        log.debug("Updating super hero {} ...", superHero);
        return superHeroRepository.save(superHero)
                .map(SuperHero::getId)
                .doOnError(error -> log.error(String.format("Error encountered while saving '%s' into the database!", superHero), error))
                .retry(3)
                .doOnSuccess(result -> log.debug("Successfully saved entity with identifier {}", result))
                .timeout(Duration.ofSeconds(5))
                .onErrorStop();
    }

}
