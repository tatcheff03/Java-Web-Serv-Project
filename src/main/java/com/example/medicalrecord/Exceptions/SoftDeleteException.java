package com.example.medicalrecord.Exceptions;

public class SoftDeleteException extends RuntimeException{
    public SoftDeleteException(String message) {
        super(message);
    }
}
