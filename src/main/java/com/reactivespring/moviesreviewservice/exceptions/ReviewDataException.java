package com.reactivespring.moviesreviewservice.exceptions;

public class ReviewDataException extends RuntimeException {
    private String message;

    public ReviewDataException(String message) {
        super(message);
        this.message = message;
    }
}
