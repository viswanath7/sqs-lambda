package com.example.sqslambda;

import com.example.sqslambda.entity.SuperHero;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@FunctionalSpringBootTest
@AutoConfigureWebTestClient
class ServerlessApplicationTest{

    @Autowired
    private WebTestClient client;

    final String endpointPath = "/handleEntity";

    final SuperHero superHero = new SuperHero("three", "flash",Set.of("speed"));

    @Test
    @DisplayName("Should invoke the function via webflux endpoint to invoke and test it")
    void doesContainsCloud() {
        client.post()
            .uri(endpointPath)
            .body(Mono.just(superHero), SuperHero.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(result -> assertThat(result).containsIgnoringCase(superHero.getId()));
    }

}
