package com.devsu.backend.infrastructure.persistence.repository;

import com.devsu.backend.infrastructure.persistence.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query(value = "SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.accounts a LEFT JOIN FETCH a.movements",
            countQuery = "SELECT count(DISTINCT c) FROM Client c")
    Page<Client> findAll(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.accounts a LEFT JOIN FETCH a.movements")
    List<Client> findAllWithFullHierarchy();

    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.accounts a LEFT JOIN FETCH a.movements WHERE c.id = :id")
    Optional<Client> findByIdWithFullHierarchy(@Param("id") Long id);

    @Query(value = "SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.accounts a LEFT JOIN FETCH a.movements " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.gender) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR CAST(c.age AS string) LIKE CONCAT('%', :term, '%') " +
            "OR LOWER(c.identification) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.clientId) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.password) LIKE LOWER(CONCAT('%', :term, '%'))",
            countQuery = "SELECT count(DISTINCT c) FROM Client c " +
            "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.gender) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR CAST(c.age AS string) LIKE CONCAT('%', :term, '%') " +
            "OR LOWER(c.identification) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.address) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.clientId) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(c.password) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<Client> searchAll(@Param("term") String term, Pageable pageable);
}
