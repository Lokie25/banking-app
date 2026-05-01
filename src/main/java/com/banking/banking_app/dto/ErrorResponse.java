package com.banking.banking_app.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
}