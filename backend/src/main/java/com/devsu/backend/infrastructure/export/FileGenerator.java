package com.devsu.backend.infrastructure.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * FileGenerator creates PDF and JSON files from given data maps.
 */
@Component
@RequiredArgsConstructor
public class FileGenerator {

    private final ObjectMapper objectMapper;

    public byte[] generatePdf(List<Map<String, Object>> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();
            if (data != null && !data.isEmpty()) {
                Object[] columnNames = data.get(0).keySet().toArray();
                PdfPTable table = new PdfPTable(columnNames.length);
                Stream.of(columnNames).map(col -> new Phrase(String.valueOf(col))).forEach(table::addCell);
                data.forEach(row -> Stream.of(columnNames).forEach(col -> table.addCell(String.valueOf(row.getOrDefault(col, "")))));
                document.add(table);
            }
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error funcional generando PDF", e);
        }
    }

    public byte[] generateJson(List<Map<String, Object>> data) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error funcional generando JSON", e);
        }
    }
}