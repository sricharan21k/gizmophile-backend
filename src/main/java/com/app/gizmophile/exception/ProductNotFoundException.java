package com.app.gizmophile.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException() {
        super("Product doesn't exist!");
    }
}
