package com.devsu.backend.infrastructure.persistence.repository;

import com.devsu.backend.infrastructure.persistence.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.movements",
            countQuery = "SELECT count(a) FROM Account a")
    Page<Account> findAll(Pageable pageable);

    @Query("SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.movements WHERE a.id = :id")
    Optional<Account> findByIdWithMovements(@Param("id") Long id);

    @Query("SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.movements")
    List<Account> findAllWithMovements();
}