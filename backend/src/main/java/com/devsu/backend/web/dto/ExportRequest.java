package com.devsu.backend.web.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/** DTO representing a request to export data with a specified file name and list of data maps. */
@Data
public class ExportRequest {
    private String fileName;
    private List<Map<String, Object>> data;
}