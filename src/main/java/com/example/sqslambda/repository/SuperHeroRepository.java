package com.example.sqslambda.repository;

import com.example.sqslambda.entity.SuperHero;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SuperHeroRepository extends ReactiveMongoRepository<SuperHero, String> {

    @Query("{'name':?0}")
    Mono<SuperHero> findByNameContains(final String name);
}
