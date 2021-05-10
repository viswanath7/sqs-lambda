package com.example.sqslambda.service;

import com.example.sqslambda.entity.SuperHero;
import com.example.sqslambda.repository.SuperHeroRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SuperHeroService {

    private final SuperHeroRepository superHeroRepository;

    public SuperHeroService(final SuperHeroRepository superHeroRepository) {
        this.superHeroRepository = superHeroRepository;
    }

    public Mono<String> update(final SuperHero superHero) {
        log.debug("Updating super hero {} ...", superHero);
        return superHeroRepository.save(superHero).map(SuperHero::getId);
    }


}
