package com.devsu.backend.web.controller;

import com.devsu.backend.application.bus.ActionType;
import com.devsu.backend.application.bus.CommandBus;
import com.devsu.backend.application.bus.QueryBus;
import com.devsu.backend.domain.factory.ActionFactory;
import com.devsu.backend.domain.factory.ResponseWrapperFactory;
import com.devsu.backend.infrastructure.persistence.Account;
import com.devsu.backend.web.config.MessageProvider;
import com.devsu.backend.web.dto.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class AccountController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final MessageProvider messageProvider;

    @PostMapping
    public ResponseEntity<ResponseWrapper<String>> createAccount(@Valid @RequestBody Account account) {
        boolean success = commandBus.dispatch(ActionFactory.createAccountAction(ActionType.CREATE, account));
        String message = success ? messageProvider.getAccountCreated() : messageProvider.getAccountError();

        return ResponseEntity.status(success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.CREATED, HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> updateAccount(@PathVariable Long id, @Valid @RequestBody Account account) {
        account.setId(id);
        boolean success = commandBus.dispatch(ActionFactory.createAccountAction(ActionType.UPDATE, account));
        String message = success ? messageProvider.getAccountUpdated() : messageProvider.getAccountError();

        return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.OK, HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteAccount(@PathVariable Long id) {
        boolean success = commandBus.dispatch(ActionFactory.createAccountAction(ActionType.DELETE, id));
        String message = success ? messageProvider.getAccountDeleted() : messageProvider.getAccountError();

        return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.OK, HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Account>> getAccount(@PathVariable Long id) {
        Account account = queryBus.dispatch(ActionFactory.createAccountAction(ActionType.GET_BY_ID, id));

        return ResponseEntity.status(account != null ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(ResponseWrapperFactory.singleResponse(account, HttpStatus.OK, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllAccounts(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        // El bus ahora devuelve el objeto Page completo (con metadatos)
        Page<Account> page = (Page<Account>) queryBus.dispatch(
                ActionFactory.createAccountAction(ActionType.GET_ALL, pageable)
        );

        return ResponseEntity.ok(
                ResponseWrapperFactory.paginatedResponse(page, HttpStatus.OK)
        );
    }
}