package com.example.sqslambda.service;

import com.example.sqslambda.entity.SuperHero;
import com.example.sqslambda.repository.SuperHeroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuperHeroServiceTest {

    @InjectMocks
    private SuperHeroService superHeroService;
    @Mock
    private SuperHeroRepository superHeroRepository;

    @Test
    @DisplayName("Super hero service must delegate to super hero repository and save correctly")
    void should_save_correctly() {
        final var superHero = new SuperHero("one", "bat man", Set.of("brilliant", "tactical", "perseverant", "combat", "inventive", "brave", "rich"));
        when(superHeroRepository.save(superHero)).thenReturn(Mono.justOrEmpty(superHero));
        superHeroService.save(superHero)
                .as(StepVerifier::create)
                .expectNext(superHero.getId())
                .expectComplete()
                .verify();
        verify(superHeroRepository).save(superHero);
    }
}