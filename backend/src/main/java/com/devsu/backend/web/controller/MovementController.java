package com.devsu.backend.web.controller;

import com.devsu.backend.application.bus.ActionType;
import com.devsu.backend.application.bus.CommandBus;
import com.devsu.backend.application.bus.QueryBus;
import com.devsu.backend.domain.factory.ActionFactory;
import com.devsu.backend.domain.factory.ResponseWrapperFactory;
import com.devsu.backend.infrastructure.persistence.Movement;
import com.devsu.backend.web.config.MessageProvider;
import com.devsu.backend.web.dto.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // IMPORTANTE: Usamos Page
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovementController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;
    private final MessageProvider messageProvider;

    @PostMapping
    public ResponseEntity<ResponseWrapper<String>> createMovement(@Valid @RequestBody Movement movement) {
        boolean success = commandBus.dispatch(ActionFactory.createMovementAction(ActionType.CREATE, movement));
        String message = success ? messageProvider.getMovementCreated() : messageProvider.getMovementError();

        return ResponseEntity.status(success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.CREATED, HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> updateMovement(@PathVariable Long id, @Valid @RequestBody Movement movement) {
        movement.setId(id);
        boolean success = commandBus.dispatch(ActionFactory.createMovementAction(ActionType.UPDATE, movement));
        String message = success ? messageProvider.getMovementUpdated() : messageProvider.getMovementError();

        return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.OK, HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteMovement(@PathVariable Long id) {
        boolean success = commandBus.dispatch(ActionFactory.createMovementAction(ActionType.DELETE, id));
        String message = success ? messageProvider.getMovementDeleted() : messageProvider.getMovementError();

        return ResponseEntity.status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(ResponseWrapperFactory.commandResponse(success, message, HttpStatus.OK, HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Movement>> getMovement(@PathVariable Long id) {
        Movement movement = queryBus.dispatch(ActionFactory.createMovementAction(ActionType.GET_BY_ID, id));

        return ResponseEntity.status(movement != null ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(ResponseWrapperFactory.singleResponse(movement, HttpStatus.OK, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllMovements(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        // El bus ahora devuelve el objeto Page completo con su metadata
        Page<Movement> page = (Page<Movement>) queryBus.dispatch(
                ActionFactory.createMovementAction(ActionType.GET_ALL, pageable)
        );

        return ResponseEntity.ok(
                ResponseWrapperFactory.paginatedResponse(page, HttpStatus.OK)
        );
    }
}