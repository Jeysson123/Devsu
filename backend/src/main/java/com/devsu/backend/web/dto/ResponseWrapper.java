package com.devsu.backend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseWrapper <T> {
    private int code;
    private boolean success;
    private T data;
}