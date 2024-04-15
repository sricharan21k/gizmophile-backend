package com.app.gizmophile.exception;

public class ReviewNotFoundException extends RuntimeException{
    public ReviewNotFoundException() {
        super("Review doesn't exist!");
    }
}
