package com.app.gizmophile.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User " + username + " doesn't exist!");
    }
}
