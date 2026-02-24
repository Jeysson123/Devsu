package com.devsu.backend.infrastructure.persistence.repository;

import com.devsu.backend.infrastructure.persistence.Movement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * MovementRepository provides data access methods for Movement entities, including account joins and search queries.
 */
public interface MovementRepository extends JpaRepository<Movement, Long> {

    @Query(value = "SELECT m FROM Movement m JOIN FETCH m.account",
            countQuery = "SELECT count(m) FROM Movement m")
    Page<Movement> findAll(Pageable pageable);

    List<Movement> findByAccountId(Long accountId);

    @Query("SELECT m FROM Movement m JOIN FETCH m.account")
    List<Movement> findAllWithAccount();

    @Query("SELECT m FROM Movement m JOIN FETCH m.account WHERE m.id = :id")
    Optional<Movement> findByIdWithAccount(@Param("id") Long id);

    @Query(value = "SELECT m FROM Movement m JOIN FETCH m.account " +
            "WHERE LOWER(m.movementType) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR CAST(m.amount AS string) LIKE CONCAT('%', :term, '%') " +
            "OR CAST(m.balance AS string) LIKE CONCAT('%', :term, '%') " +
            "OR CAST(m.date AS string) LIKE CONCAT('%', :term, '%')",
            countQuery = "SELECT count(m) FROM Movement m " +
            "WHERE LOWER(m.movementType) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR CAST(m.amount AS string) LIKE CONCAT('%', :term, '%') " +
            "OR CAST(m.balance AS string) LIKE CONCAT('%', :term, '%') " +
            "OR CAST(m.date AS string) LIKE CONCAT('%', :term, '%')")
    Page<Movement> searchAll(@Param("term") String term, Pageable pageable);
}
