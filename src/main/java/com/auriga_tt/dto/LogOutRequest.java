package com.auriga_tt.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogOutRequest {
    @NotNull
    private Long userId;
}