package com.devsu.backend.web.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ExportRequest {
    private String fileName;
    private List<Map<String, Object>> data;
}