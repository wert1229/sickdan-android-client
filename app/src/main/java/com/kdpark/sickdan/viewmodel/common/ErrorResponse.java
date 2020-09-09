package com.kdpark.sickdan.viewmodel.common;

import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private int status;
    private List<FieldError> errors;
    private String code;

    @Data
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
    }
}
