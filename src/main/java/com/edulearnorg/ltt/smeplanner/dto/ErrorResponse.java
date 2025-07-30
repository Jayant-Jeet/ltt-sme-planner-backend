package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response containing error code and message")
public class ErrorResponse {
    
    @Schema(description = "HTTP status code", example = "401")
    private int code;
    
    @Schema(description = "Error message description", example = "Invalid credentials")
    private String message;
    
    public ErrorResponse() {}
    
    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
