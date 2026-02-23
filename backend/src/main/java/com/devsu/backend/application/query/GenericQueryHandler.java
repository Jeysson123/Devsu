package com.devsu.backend.application.query;

import com.devsu.backend.application.bus.ActionType;
import com.devsu.backend.application.bus.EntityType;
import com.devsu.backend.application.bus.GenericAction;
import com.devsu.backend.application.bus.IQueryHandler;
import com.devsu.backend.infrastructure.persistence.repository.AccountRepository;
import com.devsu.backend.infrastructure.persistence.repository.ClientRepository;
import com.devsu.backend.infrastructure.persistence.repository.MovementRepository;
import com.devsu.backend.infrastructure.persistence.repository.ReportRepository;
import com.devsu.backend.web.dto.ReportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenericQueryHandler implements IQueryHandler<GenericAction, Object> {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final ReportRepository reportRepository;

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
                if (payload instanceof Pageable) {
                    return movementRepository.findAll((Pageable) payload);
                }
                return movementRepository.findAll();
            default:
                throw new IllegalArgumentException("Unsupported query action: " + action);
        }
    }

    private Object handleReportQuery(ActionType action, Object payload) {
        if (action == ActionType.GET_ALL && payload instanceof ReportRequest) {
            ReportRequest request = (ReportRequest) payload;

            return reportRepository.getAccountReport(
                    request.getClientName(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getPageable()
            );
        }
        throw new IllegalArgumentException("Unsupported report action or invalid payload");
    }
}