package com.devsu.backend.web.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReportRequest {
    private final String clientName;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Pageable pageable;
    private final String searchTerm;

    public boolean hasSearch() {
        return searchTerm != null && !searchTerm.isBlank();
    }
}
