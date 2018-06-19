package com.capitalone.dashboard.auth.exceptions;


public class CreateUserException extends RuntimeException {
    private static final long serialVersionUID = -8596676033217258687L;

    public CreateUserException(String message) { 
        super(message);
    }
}
