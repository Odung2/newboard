package com.wemade.newboard.exception;

public class UnauthorizedAccessException extends RuntimeException{

    public UnauthorizedAccessException(){}

    public UnauthorizedAccessException(String message){
        super(message);
    }

    public UnauthorizedAccessException(Throwable throwable){
        super(throwable);
    }

    public UnauthorizedAccessException(String message, Throwable throwable){
        super(message, throwable);
    }
}
