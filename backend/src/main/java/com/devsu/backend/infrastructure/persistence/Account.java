package com.devsu.backend.infrastructure.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Account entity representing bank accounts with client association and movements.
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Account number is mandatory")
    @Size(min = 10, max = 20)
    @Column(unique = true, nullable = false)
    private String accountNumber;

    @NotBlank(message = "Account type is mandatory")
    private String accountType;

    @NotNull(message = "Initial balance is mandatory")
    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    private Double initialBalance;

    @NotNull(message = "Status is mandatory")
    private Boolean status;

    @NotNull(message = "Client is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"accounts", "password"})
    private Client client;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnoreProperties({"account"})
    private Set<Movement> movements = new HashSet<>();

}
