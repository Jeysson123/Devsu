package com.devsu.backend.infrastructure.persistence;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client extends Person {

    @Size(min = 5, max = 20)
    private String clientId;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 20)
    private String password;

    @NotNull(message = "Status is mandatory")
    private Boolean status;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference
    private Set<Account> accounts = new HashSet<>();
}