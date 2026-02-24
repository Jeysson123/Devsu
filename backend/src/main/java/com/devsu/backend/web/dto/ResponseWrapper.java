package com.devsu.backend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** Generic wrapper for API responses, including HTTP status code, success flag, and payload of type T. */
@Getter
@Setter
@AllArgsConstructor
public class ResponseWrapper <T> {
    private int code;
    private boolean success;
    private T data;
}