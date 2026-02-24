package com.devsu.backend;

import com.devsu.backend.application.bus.*;
import com.devsu.backend.application.query.GenericQueryHandler;
import com.devsu.backend.domain.factory.ActionFactory;
import com.devsu.backend.infrastructure.persistence.*;
import com.devsu.backend.infrastructure.persistence.repository.*;
import com.devsu.backend.web.dto.ReportRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericQueryHandlerTest {

    @Mock private ClientRepository clientRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private MovementRepository movementRepository;
    @Mock private ReportRepository reportRepository;
    @InjectMocks private GenericQueryHandler queryHandler;

    @Test
    void testClientQueries() {
        when(clientRepository.findByIdWithFullHierarchy(1L)).thenReturn(Optional.of(mock(Client.class)));
        assertNotNull(queryHandler.handle(ActionFactory.createClientAction(ActionType.GET_BY_ID, 1L)));

        // Solución: Usar una lista tipada explícitamente
        when(clientRepository.findAllWithFullHierarchy()).thenReturn(new ArrayList<Client>());
        assertNotNull(queryHandler.handle(ActionFactory.createClientAction(ActionType.GET_ALL, null)));
    }

    @Test
    void testAccountQueries() {
        when(accountRepository.findByIdWithMovements(1L)).thenReturn(Optional.of(mock(Account.class)));
        assertNotNull(queryHandler.handle(ActionFactory.createAccountAction(ActionType.GET_BY_ID, 1L)));

        when(accountRepository.findAllWithMovements()).thenReturn(new ArrayList<Account>());
        assertNotNull(queryHandler.handle(ActionFactory.createAccountAction(ActionType.GET_ALL, null)));
    }

    @Test
    void testMovementQueries() {
        when(movementRepository.findById(1L)).thenReturn(Optional.of(mock(Movement.class)));
        assertNotNull(queryHandler.handle(ActionFactory.createMovementAction(ActionType.GET_BY_ID, 1L)));

        when(movementRepository.findAll()).thenReturn(new ArrayList<Movement>());
        assertNotNull(queryHandler.handle(ActionFactory.createMovementAction(ActionType.GET_ALL, null)));
    }

    @Test
    void testReportQueries() {
        ReportRequest mockRequest = mock(ReportRequest.class);

        doReturn(Collections.emptyList())
                .when(reportRepository)
                .getAccountReport(any(), any(), any(), any(), any());

        Object result = queryHandler.handle(ActionFactory.createReportAction(ActionType.GET_ALL, mockRequest));

        assertNotNull(result);
        verify(reportRepository).getAccountReport(any(), any(), any(), any(), any());
    }
}