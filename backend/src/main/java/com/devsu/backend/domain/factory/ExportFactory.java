package com.devsu.backend.domain.factory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ExportFactory {
    public static ResponseEntity<byte[]> createPdfResponse(byte[] content, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", fileName + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
}