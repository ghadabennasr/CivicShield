package com.civicshield.user.exception;

public class LastAdministratorException extends RuntimeException {

    public LastAdministratorException() {
        super("The last administrator cannot be demoted.");
    }
}