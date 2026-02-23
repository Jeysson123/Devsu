package com.devsu.backend.web.controller;

import com.devsu.backend.application.bus.ActionType;
import com.devsu.backend.application.bus.CommandBus;
import com.devsu.backend.application.bus.QueryBus;
import com.devsu.backend.domain.factory.ActionFactory;
import com.devsu.backend.domain.factory.ResponseWrapperFactory;
import com.devsu.backend.infrastructure.persistence.Client;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final MessageProvider messageProvider;

    @PostMapping
    public ResponseEntity<ResponseWrapper<String>> createClient(@Valid @RequestBody Client client) {
        boolean success = commandBus.dispatch(ActionFactory.createClientAction(ActionType.CREATE, client));
        String message = success ? messageProvider.getClientCreated() : messageProvider.getClientError();

        return ResponseEntity.status(success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.CREATED, HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> updateClient(@PathVariable Long id, @Valid @RequestBody Client client) {
        client.setId(id);
        boolean success = commandBus.dispatch(ActionFactory.createClientAction(ActionType.UPDATE, client));
        String message = success ? messageProvider.getClientUpdated() : messageProvider.getClientError();

        return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.OK, HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteClient(@PathVariable Long id) {
        boolean success = commandBus.dispatch(ActionFactory.createClientAction(ActionType.DELETE, id));
        String message = success ? messageProvider.getClientDeleted() : messageProvider.getClientError();

        return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.OK, HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Client>> getClient(@PathVariable Long id) {
        Client client = queryBus.dispatch(ActionFactory.createClientAction(ActionType.GET_BY_ID, id));

        return ResponseEntity.status(client != null ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(ResponseWrapperFactory.singleResponse(client, HttpStatus.OK, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllClients(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        // El dispatch devuelve el Page<Client> completo gracias al cambio en GenericQueryHandler
        Page<Client> page = (Page<Client>) queryBus.dispatch(
                ActionFactory.createClientAction(ActionType.GET_ALL, pageable)
        );

        return ResponseEntity.ok(
                ResponseWrapperFactory.paginatedResponse(page, HttpStatus.OK)
        );
    }
}