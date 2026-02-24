package com.devsu.backend.web.dto;

import org.springframework.data.domain.Pageable;

public record SearchRequest(Pageable pageable, String searchTerm) {

    public boolean hasSearch() {
        return searchTerm != null && !searchTerm.isBlank();
    }
}
