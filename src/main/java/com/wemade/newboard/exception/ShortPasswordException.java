package com.wemade.newboard.exception;

public class ShortPasswordException extends RuntimeException{
    public ShortPasswordException(){}

    public ShortPasswordException(String message){
        super(message);
    }

    public ShortPasswordException(Throwable throwable){
        super(throwable);
    }

    public ShortPasswordException(String message, Throwable throwable){
        super(message, throwable);
    }
}
