package com.example.certif_app.exceptions;

public class CertificateGenerationException extends RuntimeException {
    public CertificateGenerationException(String message) {
        super(message);
    }

    public CertificateGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}