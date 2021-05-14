package com.example.sqslambda.converter;

import com.example.sqslambda.entity.SuperHero;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.NonNull;
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
                .flatMap(this::extractBody)
                .flatMap(json -> extractEntity(json, SuperHero.class))
                .orElseThrow( () -> new RuntimeException("Failed to extract an object of type SuperHero from message!") );
    }

    private Optional<String> extractBody(@NonNull final String sqsJSONPayload) {
        log.debug("Extract the value of 'body' property of first 'Record' from \n {}", sqsJSONPayload);
        try {
            return Optional.ofNullable(JsonPath.parse(sqsJSONPayload).read("$['Records'][0]['body']", String.class));
        } catch (PathNotFoundException pathNotFoundException) {
            log.warn("Failed to extract the value for 'body' property from SQS event JSON {}", sqsJSONPayload);
            return Optional.of(sqsJSONPayload);
        }
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
