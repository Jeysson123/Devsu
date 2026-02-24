package com.devsu.backend.domain.factory;

import com.devsu.backend.web.dto.ReportRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * ReportFactory creates ReportRequest instances with specified parameters.
 */
public final class ReportFactory {

    private ReportFactory() {
    }

    public static ReportRequest createRequest(String clientName, LocalDateTime startDate,
                                              LocalDateTime endDate, Pageable pageable, String searchTerm) {
        return ReportRequest.builder()
                .clientName(clientName)
                .startDate(startDate)
                .endDate(endDate)
                .pageable(pageable)
                .searchTerm(searchTerm)
                .build();
    }
}