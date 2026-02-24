package com.devsu.backend.domain.factory;

import com.devsu.backend.web.dto.ResponseWrapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * ResponseWrapperFactory creates standardized API response wrappers for various scenarios.
 */
public class ResponseWrapperFactory {

    public static <T> ResponseWrapper<T> successResponse(T data, HttpStatus status) {
        return new ResponseWrapper<>(
                status.value(),
                true,
                data
        );
    }

    public static <T> ResponseWrapper<T> listResponse(T data, HttpStatus successStatus, HttpStatus errorStatus) {
        return new ResponseWrapper<>(
                data != null ? successStatus.value() : errorStatus.value(),
                data != null,
                data
        );
    }

    public static <T> ResponseWrapper<Map<String, Object>> paginatedResponse(Page<T> page, HttpStatus successStatus) {
        Map<String, Object> paginatedData = new HashMap<>();
        paginatedData.put("content", page.getContent());
        paginatedData.put("totalElements", page.getTotalElements());
        paginatedData.put("totalPages", page.getTotalPages());
        paginatedData.put("currentPage", page.getNumber());
        paginatedData.put("pageSize", page.getSize());

        return new ResponseWrapper<>(successStatus.value(), true, paginatedData);
    }

    public static ResponseWrapper<String> commandResponse(boolean success, String message, HttpStatus successStatus, HttpStatus errorStatus) {
        return new ResponseWrapper<>(
                success ? successStatus.value() : errorStatus.value(),
                success,
                message
        );
    }

    public static <T> ResponseWrapper<T> singleResponse(T data, HttpStatus successStatus, HttpStatus errorStatus) {
        return new ResponseWrapper<>(
                data != null ? successStatus.value() : errorStatus.value(),
                data != null,
                data
        );
    }
}