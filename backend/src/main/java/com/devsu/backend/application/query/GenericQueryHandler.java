package com.devsu.backend.application.query;

import com.devsu.backend.application.bus.ActionType;
import com.devsu.backend.application.bus.EntityType;
import com.devsu.backend.application.bus.GenericAction;
import com.devsu.backend.application.bus.IQueryHandler;
import com.devsu.backend.application.service.DateService;
import com.devsu.backend.infrastructure.persistence.Movement;
import com.devsu.backend.infrastructure.persistence.repository.AccountRepository;
import com.devsu.backend.infrastructure.persistence.repository.ClientRepository;
import com.devsu.backend.infrastructure.persistence.repository.MovementRepository;
import com.devsu.backend.infrastructure.persistence.repository.ReportRepository;
import com.devsu.backend.web.dto.ReportRequest;
import com.devsu.backend.web.dto.SearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * GenericQueryHandler processes GenericAction queries for clients, accounts, movements, and reports.
 */
@Service
@RequiredArgsConstructor
public class GenericQueryHandler implements IQueryHandler<GenericAction, Object> {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final ReportRepository reportRepository;
    private final DateService dateService;

    @Override
    @Transactional(readOnly = true)
    public Object handle(GenericAction query) {
        EntityType e = query.entityType();
        ActionType a = query.action();
        Object payload = query.payload();

        switch (e) {
            case CLIENT:
                return handleClientQuery(a, payload);
            case ACCOUNT:
                return handleAccountQuery(a, payload);
            case MOVEMENT:
                return handleMovementQuery(a, payload);
            case REPORT:
                return handleReportQuery(a, payload);
            default:
                throw new IllegalArgumentException("Unsupported entity: " + e);
        }
    }

    private Object handleClientQuery(ActionType action, Object payload) {
        switch (action) {
            case GET_BY_ID:
                return clientRepository.findByIdWithFullHierarchy((Long) payload).orElse(null);
            case GET_ALL:
                if (payload instanceof SearchRequest sr) {
                    return sr.hasSearch()
                            ? clientRepository.searchAll(sr.searchTerm(), sr.pageable())
                            : clientRepository.findAll(sr.pageable());
                }
                if (payload instanceof Pageable) {
                    return clientRepository.findAll((Pageable) payload);
                }
                return clientRepository.findAllWithFullHierarchy();
            default:
                throw new IllegalArgumentException("Action not supported");
        }
    }

    private Object handleAccountQuery(ActionType action, Object payload) {
        switch (action) {
            case GET_BY_ID:
                if (!(payload instanceof Long)) return null;
                return accountRepository.findByIdWithMovements((Long) payload).orElse(null);
            case GET_ALL:
                if (payload instanceof SearchRequest sr) {
                    return sr.hasSearch()
                            ? accountRepository.searchAll(sr.searchTerm(), sr.pageable())
                            : accountRepository.findAll(sr.pageable());
                }
                if (payload instanceof Pageable) {
                    return accountRepository.findAll((Pageable) payload);
                }
                return accountRepository.findAllWithMovements();
            default:
                throw new IllegalArgumentException("Unsupported query action: " + action);
        }
    }

    private Object handleMovementQuery(ActionType action, Object payload) {
        switch (action) {
            case GET_BY_ID:
                if (!(payload instanceof Long)) return null;
                return movementRepository.findById((Long) payload).orElse(null);
            case GET_ALL:
                Page<Movement> movementsPage;

                if (payload instanceof SearchRequest sr) {
                    movementsPage = sr.hasSearch()
                            ? movementRepository.searchAll(sr.searchTerm(), sr.pageable())
                            : movementRepository.findAll(sr.pageable());
                } else if (payload instanceof Pageable pageable) {
                    movementsPage = movementRepository.findAll(pageable);
                } else {
                    var list = movementRepository.findAll();
                    movementsPage = new PageImpl<>(list);
                }

                var mappedContent = movementsPage.getContent().stream()
                        .map(m -> Map.of(
                                "id", m.getId(),
                                "date", m.getDate() != null ? dateService.formatToDayMonthYear(m.getDate().toString()) : "",
                                "movementType", m.getMovementType(),
                                "amount", m.getAmount(),
                                "balance", m.getBalance(),
                                "accountId", m.getAccount() != null ? m.getAccount().getId() : null,
                                "accountNumber", m.getAccount() != null ? m.getAccount().getAccountNumber() : null
                        ))
                        .collect(Collectors.toList());

                return new PageImpl<>(mappedContent, movementsPage.getPageable(), movementsPage.getTotalElements());
            default:
                throw new IllegalArgumentException("Unsupported query action: " + action);
        }
    }

    private Object handleReportQuery(ActionType action, Object payload) {
        if (action == ActionType.GET_ALL && payload instanceof ReportRequest) {
            ReportRequest request = (ReportRequest) payload;

            var reportsPage = reportRepository.getAccountReport(
                    request.getClientName(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getPageable(),
                    request.getSearchTerm()
            );

            var mappedContent = reportsPage.getContent().stream()
                    .map(r -> Map.of(
                            "date", r.getDate() != null ? dateService.formatToDayMonthYear(r.getDate().toString()) : "",
                            "clientName", r.getClientName(),
                            "accountNumber", r.getAccountNumber(),
                            "accountType", r.getAccountType(),
                            "initialBalance", r.getInitialBalance(),
                            "accountStatus", r.getAccountStatus(),
                            "movementAmount", r.getMovementAmount(),
                            "availableBalance", r.getAvailableBalance()
                    ))
                    .collect(Collectors.toList());

            return new PageImpl<>(mappedContent, reportsPage.getPageable(), reportsPage.getTotalElements());
        }
        throw new IllegalArgumentException("Unsupported report action or invalid payload");
    }
}