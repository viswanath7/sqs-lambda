package com.example.sqslambda;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@FunctionalSpringBootTest
@AutoConfigureWebTestClient
class ServerlessApplicationTest {

    @Autowired
    private WebTestClient client;

    final String endpointPath = "/isGreeting";

    @Disabled
    @Test
    void doesContainsCloud() {
        client.post().uri(endpointPath).body(Mono.just("Hi there!"), String.class).exchange()
                .expectStatus().isOk().expectBody(String.class).isEqualTo("true");
    }

    @Disabled
    @Test
    void doesNotContainsCloud() {
        client.post().uri(endpointPath).body(Mono.just("Not a greeting"), String.class).exchange()
                .expectStatus().isOk().expectBody(String.class).isEqualTo("false");
    }

}
