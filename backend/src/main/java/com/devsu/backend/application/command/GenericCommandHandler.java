package com.devsu.backend.application.command;

import com.devsu.backend.application.bus.*;
import com.devsu.backend.domain.factory.*;
import com.devsu.backend.domain.service.AccountValidatorService;
import com.devsu.backend.infrastructure.export.FileGenerator;
import com.devsu.backend.infrastructure.persistence.*;
import com.devsu.backend.infrastructure.persistence.repository.*;
import com.devsu.backend.web.config.MessageProvider;
import com.devsu.backend.web.dto.ExportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenericCommandHandler implements ICommandHandler<GenericAction, Object> {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final MessageProvider messageProvider;
    private final AccountValidatorService accountValidatorService;
    private final FileGenerator fileGenerator;

    @Override
    @Transactional
    public Object handle(GenericAction command) {
        switch (command.entityType()) {
            case CLIENT: return handleClient(command.action(), command.payload());
            case ACCOUNT: return handleAccount(command.action(), command.payload());
            case MOVEMENT: return handleMovement(command.action(), command.payload());
            case REPORT: return handleReport(command.action(), command.payload());
            default: throw new IllegalArgumentException("Unsupported entity: " + command.entityType());
        }
    }

    private Object handleReport(ActionType action, Object payload) {
        if (action == ActionType.EXECUTE && payload instanceof ExportRequest) {
            ExportRequest request = (ExportRequest) payload;
            // Se adiciona la lógica de exportación sin borrar nada anterior
            return Map.of(
                    "pdf", fileGenerator.generatePdf(request.getData()),
                    "json", fileGenerator.generateJson(request.getData())
            );
        }
        throw new IllegalArgumentException("Acción de reporte no soportada");
    }

    private Object handleClient(ActionType action, Object payload) {
        if (action == ActionType.CREATE && payload instanceof Client) {
            Client c = (Client) payload;
            return clientRepository.save(ClientFactory.create(c.getName(), c.getGender(), c.getAge(),
                    c.getIdentification(), c.getAddress(), c.getPhone(), c.getClientId(),
                    c.getPassword(), c.getStatus())) != null;
        } else if (action == ActionType.UPDATE && payload instanceof Client) {
            Client c = (Client) payload;
            return clientRepository.findById(c.getId()).map(db -> {
                db.setName(c.getName()); db.setGender(c.getGender()); db.setAge(c.getAge());
                db.setIdentification(c.getIdentification()); db.setAddress(c.getAddress());
                db.setPhone(c.getPhone()); db.setStatus(c.getStatus());
                return clientRepository.save(db) != null;
            }).orElse(false);
        } else if (action == ActionType.DELETE && payload instanceof Long) {
            if (!clientRepository.existsById((Long) payload)) return false;
            clientRepository.deleteById((Long) payload);
            return true;
        }
        return false;
    }

    private Object handleAccount(ActionType action, Object payload) {
        if (action == ActionType.CREATE && payload instanceof Account) {
            Account a = (Account) payload;
            return accountRepository.save(AccountFactory.create(a.getAccountNumber(),
                    a.getAccountType(), a.getInitialBalance(), a.getStatus(), a.getClient())) != null;
        } else if (action == ActionType.UPDATE && payload instanceof Account) {
            Account a = (Account) payload;
            return accountRepository.findById(a.getId()).map(db -> {
                db.setAccountNumber(a.getAccountNumber()); db.setAccountType(a.getAccountType());
                db.setInitialBalance(a.getInitialBalance()); db.setStatus(a.getStatus());
                return accountRepository.save(db) != null;
            }).orElse(false);
        } else if (action == ActionType.DELETE && payload instanceof Long) {
            if (!accountRepository.existsById((Long) payload)) return false;
            accountRepository.deleteById((Long) payload);
            return true;
        }
        return false;
    }

    private Object handleMovement(ActionType action, Object payload) {
        if (action == ActionType.CREATE && payload instanceof Movement) {
            Movement m = (Movement) payload;
            Account account = accountRepository.findById(m.getAccount().getId())
                    .orElseThrow(() -> new IllegalArgumentException(messageProvider.getAccountNotFound()));
            accountValidatorService.validateDailyLimit(account, m.getAmount(), m.getMovementType());
            Double newBalance = accountValidatorService.calculateNewBalance(account, m);
            account.setInitialBalance(newBalance);
            accountRepository.save(account);
            Movement movementToSave = MovementFactory.create(account, m.getMovementType(), m.getAmount(), newBalance);
            return movementRepository.save(movementToSave) != null;
        } else if (action == ActionType.UPDATE && payload instanceof Movement) {
            Movement m = (Movement) payload;
            return movementRepository.findById(m.getId()).map(db -> {
                db.setMovementType(m.getMovementType());
                db.setAmount(m.getAmount());
                return movementRepository.save(db) != null;
            }).orElse(false);
        } else if (action == ActionType.DELETE && payload instanceof Long) {
            if (!movementRepository.existsById((Long) payload)) return false;
            movementRepository.deleteById((Long) payload);
            return true;
        }
        return false;
    }
}