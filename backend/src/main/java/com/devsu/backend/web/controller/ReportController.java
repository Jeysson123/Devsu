package com.devsu.backend.web.controller;

import com.devsu.backend.application.bus.ActionType;
import com.devsu.backend.application.bus.CommandBus;
import com.devsu.backend.application.bus.QueryBus;
import com.devsu.backend.domain.factory.*;
import com.devsu.backend.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportController {

    private final QueryBus queryBus;
    private final CommandBus commandBus;

    @GetMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getReport(
            @RequestParam String clientName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String searchTerm) {

        var request = ReportFactory.createRequest(clientName, startDate, endDate, pageable, searchTerm);
        Page<AccountReport> report = queryBus.dispatch(ActionFactory.createReportAction(ActionType.GET_ALL, request));
        return ResponseEntity.ok(ResponseWrapperFactory.paginatedResponse(report, HttpStatus.OK));
    }

    @PostMapping("/exportar")
    public ResponseEntity<ResponseWrapper<Map<String, String>>> exportData(@RequestBody ExportRequest request) {

        Map<String, byte[]> result = commandBus.dispatch(ActionFactory.createExportAction(request));

        Map<String, String> data = Map.of(
                "fileName", request.getFileName(),
                "pdf", Base64.getEncoder().encodeToString(result.get("pdf")),
                "json", Base64.getEncoder().encodeToString(result.get("json"))
        );

        return ResponseEntity.ok(ResponseWrapperFactory.successResponse(data, HttpStatus.OK));
    }
}