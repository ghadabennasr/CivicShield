package com.civicshield.user.exception;

public class SelfRoleChangeException extends RuntimeException {

    public SelfRoleChangeException() {
        super("Administrators cannot change their own role.");
    }
}