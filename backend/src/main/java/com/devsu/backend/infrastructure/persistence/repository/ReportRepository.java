package com.devsu.backend.infrastructure.persistence.repository;

import com.devsu.backend.web.dto.AccountReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<AccountReport> getAccountReport(String clientName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String searchTerm) {
        String baseJpql = "FROM Movement m JOIN m.account a JOIN a.client c " +
                "WHERE c.name = :clientName AND m.date BETWEEN :startDate AND :endDate";

        String searchFilter = "";
        if (searchTerm != null && !searchTerm.isBlank()) {
            searchFilter = " AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :term, '%')) " +
                    "OR LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :term, '%')) " +
                    "OR LOWER(a.accountType) LIKE LOWER(CONCAT('%', :term, '%')) " +
                    "OR CAST(a.initialBalance AS string) LIKE CONCAT('%', :term, '%') " +
                    "OR CAST(m.amount AS string) LIKE CONCAT('%', :term, '%') " +
                    "OR CAST(m.balance AS string) LIKE CONCAT('%', :term, '%') " +
                    "OR CAST(m.date AS string) LIKE CONCAT('%', :term, '%'))";
        }

        String jpql = "SELECT new com.devsu.backend.web.dto.AccountReport(" +
                "m.date, c.name, a.accountNumber, a.accountType, a.initialBalance, a.status, m.amount, m.balance) " +
                baseJpql + searchFilter;

        var query = entityManager.createQuery(jpql, AccountReport.class)
                .setParameter("clientName", clientName)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        String countJpql = "SELECT COUNT(m) " + baseJpql + searchFilter;
        var countQuery = entityManager.createQuery(countJpql, Long.class)
                .setParameter("clientName", clientName)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);

        if (searchTerm != null && !searchTerm.isBlank()) {
            query.setParameter("term", searchTerm);
            countQuery.setParameter("term", searchTerm);
        }

        return new PageImpl<>(query.getResultList(), pageable, countQuery.getSingleResult());
    }
}