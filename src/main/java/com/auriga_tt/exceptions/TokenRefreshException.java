package com.auriga_tt.exceptions;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String message, String s) {
        super(message);
    }
}
