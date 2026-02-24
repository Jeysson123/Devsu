package com.devsu.backend;

import com.devsu.backend.application.bus.*;
import com.devsu.backend.application.command.GenericCommandHandler;
import com.devsu.backend.domain.factory.*;
import com.devsu.backend.domain.service.AccountValidatorService;
import com.devsu.backend.infrastructure.persistence.*;
import com.devsu.backend.infrastructure.persistence.repository.*;
import com.devsu.backend.web.config.MessageProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericCommandHandlerTest {

    @Mock private ClientRepository clientRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private MovementRepository movementRepository;
    @Mock private AccountValidatorService accountValidatorService;
    @Mock private MessageProvider messageProvider;
    @InjectMocks private GenericCommandHandler commandHandler;

    @Test
    void testClientCRUD() {
        Client mockClient = mock(Client.class);
        when(mockClient.getId()).thenReturn(1L);

        try (MockedStatic<ClientFactory> f = mockStatic(ClientFactory.class)) {
            // CREATE
            f.when(() -> ClientFactory.create(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(mockClient);
            when(clientRepository.save(mockClient)).thenReturn(mockClient);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createClientAction(ActionType.CREATE, mockClient)));

            // UPDATE
            when(clientRepository.findById(1L)).thenReturn(Optional.of(mockClient));
            when(clientRepository.save(mockClient)).thenReturn(mockClient);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createClientAction(ActionType.UPDATE, mockClient)));

            // DELETE
            when(clientRepository.existsById(1L)).thenReturn(true);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createClientAction(ActionType.DELETE, 1L)));
        }
    }

    @Test
    void testAccountCRUD() {
        Account mockAccount = mock(Account.class);
        when(mockAccount.getId()).thenReturn(1L);

        try (MockedStatic<AccountFactory> f = mockStatic(AccountFactory.class)) {
            // CREATE
            f.when(() -> AccountFactory.create(any(), any(), any(), any(), any())).thenReturn(mockAccount);
            when(accountRepository.save(mockAccount)).thenReturn(mockAccount);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createAccountAction(ActionType.CREATE, mockAccount)));

            // UPDATE
            when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
            when(accountRepository.save(mockAccount)).thenReturn(mockAccount);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createAccountAction(ActionType.UPDATE, mockAccount)));

            // DELETE
            when(accountRepository.existsById(1L)).thenReturn(true);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createAccountAction(ActionType.DELETE, 1L)));
        }
    }

    @Test
    void testMovementCRUD() {
        Movement mockMovement = mock(Movement.class);
        Account mockAccount = mock(Account.class);
        when(mockMovement.getAccount()).thenReturn(mockAccount);
        when(mockAccount.getId()).thenReturn(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

        try (MockedStatic<MovementFactory> f = mockStatic(MovementFactory.class)) {
            // CREATE
            when(accountValidatorService.calculateNewBalance(any(), any())).thenReturn(100.0);
            f.when(() -> MovementFactory.create(any(), any(), any(), any())).thenReturn(mockMovement);
            when(movementRepository.save(any())).thenReturn(mockMovement);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createMovementAction(ActionType.CREATE, mockMovement)));

            // UPDATE
            when(movementRepository.findById(1L)).thenReturn(Optional.of(mockMovement));
            when(movementRepository.save(mockMovement)).thenReturn(mockMovement);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createMovementAction(ActionType.UPDATE, mockMovement)));

            // DELETE
            when(movementRepository.existsById(1L)).thenReturn(true);
            assertTrue((Boolean) commandHandler.handle(ActionFactory.createMovementAction(ActionType.DELETE, 1L)));
        }
    }
}