package com.devsu.backend.infrastructure.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movements")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull(message = "Date is mandatory")
    private LocalDateTime date;

    @NotBlank(message = "Movement type is mandatory")
    private String movementType;

    @NotNull(message = "Amount is mandatory")
    private Double amount;

    private Double balance;

    @NotNull(message = "Account is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnoreProperties({"movements", "client"})
    private Account account;
}