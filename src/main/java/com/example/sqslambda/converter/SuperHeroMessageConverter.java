package com.example.sqslambda.converter;

import com.example.sqslambda.entity.SuperHero;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

import java.util.Optional;
import java.util.Set;

@Slf4j
public class SuperHeroMessageConverter extends AbstractMessageConverter {

    public SuperHeroMessageConverter() {
        super(new MimeType("*", "*"));
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return SuperHero.class.equals(clazz);
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        log.debug("Converting an incoming message to an object of type SuperHero ...");
        message.getHeaders().forEach( (header, value) -> log.debug("Header name: {}, Header value {}", header, value) );
        return Optional.of(message.getPayload())
                .map( payload -> payload instanceof String ? (String) payload : new String((byte[]) payload))
                .map( payloadText -> JsonPath.parse(payloadText).read("$['Records'][0]['body']", String.class))
                .flatMap(json -> extractEntity(json, SuperHero.class))
                .orElseThrow( () -> new RuntimeException("Failed to extract an object of type SuperHero from message!") );
    }

    private <T> Optional<T> extractEntity(String messagePayload, final Class<T> clazz) {
        log.debug("Converting JSON '{}' to an entity of type {}", messagePayload, clazz.getSimpleName());
        try {
            return Optional.ofNullable(new ObjectMapper().readValue(messagePayload, clazz));
        } catch (final JsonProcessingException jsonEx) {
            log.error("Error encountered while parsing JSON content: "+messagePayload, jsonEx);
            return Optional.empty();
        }
    }
}
