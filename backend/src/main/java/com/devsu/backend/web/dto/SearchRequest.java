package com.devsu.backend.web.dto;

import org.springframework.data.domain.Pageable;

/** Encapsulates pagination and optional search term for queries. */
public record SearchRequest(Pageable pageable, String searchTerm) {

    public boolean hasSearch() {
        return searchTerm != null && !searchTerm.isBlank();
    }
}
