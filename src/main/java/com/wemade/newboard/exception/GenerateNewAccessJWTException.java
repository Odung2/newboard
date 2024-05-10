package com.wemade.newboard.exception;

public class GenerateNewAccessJWTException extends RuntimeException{
    public GenerateNewAccessJWTException(){}

    public GenerateNewAccessJWTException(String message){
        super(message);
    }

    public GenerateNewAccessJWTException(Throwable throwable){
        super(throwable);
    }

    public GenerateNewAccessJWTException(String message, Throwable throwable){
        super(message, throwable);
    }
}
