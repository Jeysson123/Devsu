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

    public Page<AccountReport> getAccountReport(String clientName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // La consulta debe coincidir exactamente con el orden del constructor en AccountReport
        // Constructor: (date, clientName, accountNumber, accountType, initialBalance, accountStatus, movementAmount, availableBalance)
        String jpql = "SELECT new com.devsu.backend.web.dto.AccountReport(" +
                "m.date, c.name, a.accountNumber, a.accountType, a.initialBalance, a.status, m.amount, m.balance) " +
                "FROM Movement m " +
                "JOIN m.account a " +
                "JOIN a.client c " +
                "WHERE c.name = :clientName " +
                "AND m.date BETWEEN :startDate AND :endDate";

        List<AccountReport> content = entityManager.createQuery(jpql, AccountReport.class)
                .setParameter("clientName", clientName)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Consulta para contar el total de elementos (necesaria para la paginación)
        String countJpql = "SELECT COUNT(m) FROM Movement m JOIN m.account a JOIN a.client c " +
                "WHERE c.name = :clientName AND m.date BETWEEN :startDate AND :endDate";

        Long total = entityManager.createQuery(countJpql, Long.class)
                .setParameter("clientName", clientName)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}