package com.app.gizmophile.exception;

public class StockNotAvailableException extends RuntimeException{
    public StockNotAvailableException(String productName) {
        super("Product "+productName+" is out of stock!");
    }
}
