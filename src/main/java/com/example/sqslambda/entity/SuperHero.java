package com.example.sqslambda.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "superheroes")
@ToString
public class SuperHero {
    @Id
    @ToString.Exclude
    private String id;
    private String name;
    private Set<String> powers;
}
