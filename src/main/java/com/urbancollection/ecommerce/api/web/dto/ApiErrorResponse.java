package com.urbancollection.ecommerce.api.web.dto;

import java.util.List;

public class ApiErrorResponse {

    private String error;
    private List<String> details;

    public ApiErrorResponse(String error, List<String> details) {
        this.error = error;
        this.details = details;
    }

    public String getError() {
        return error;
    }

    public List<String> getDetails() {
        return details;
    }
}