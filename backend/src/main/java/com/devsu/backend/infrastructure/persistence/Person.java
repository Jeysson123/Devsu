package com.devsu.backend.infrastructure.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Gender is mandatory")
    private String gender;

    @NotNull(message = "Age is mandatory")
    @Min(value = 18, message = "Must be at least 18 years old")
    private Integer age;

    @NotBlank(message = "Identification is mandatory")
    @Size(min = 8, max = 20)
    private String identification;

    @Size(max = 200)
    private String address;

    @Size(max = 15)
    private String phone;
}