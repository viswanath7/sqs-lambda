package com.example.sqslambda.repository;

import com.example.sqslambda.entity.SuperHero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

@DataMongoTest
public class SuperHeroRepositoryTest {

    @Autowired
    private ReactiveMongoOperations operations;

    @Autowired
    private SuperHeroRepository repository;

    @BeforeEach
    void setUp() {
        var recreateCollection = operations.collectionExists(SuperHero.class) //
                .flatMap(exists -> exists ? operations.dropCollection(SuperHero.class) : Mono.just(exists)) //
                .then(operations.createCollection(SuperHero.class, CollectionOptions.empty() //
                        .size(1024 * 1024) //
                        .maxDocuments(100) //
                        .capped()));
        recreateCollection.as(StepVerifier::create).expectNextCount(1).verifyComplete();
        var insertAll = operations.insertAll(Flux.just(
                new SuperHero("one", "bat man", Set.of("brilliant", "tactical", "perseverant", "combat", "inventive", "brave", "rich")),
                new SuperHero("two", "super man", Set.of("strength", "speed", "flight", "x-ray and heat vision")),
                new SuperHero("three", "flash", Set.of("speed"))
        ).collectList());
        insertAll.as(StepVerifier::create).expectNextCount(3).verifyComplete();
    }

    @Test
    @DisplayName("Super hero repository must successfully fetch saved entity")
    void should_fetch_saved_entity() {
        repository.findByName("flash")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
}