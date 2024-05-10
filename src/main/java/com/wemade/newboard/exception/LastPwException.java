package com.wemade.newboard.exception;

public class LastPwException extends RuntimeException{
    public LastPwException(){}

    public LastPwException(String message){
        super(message);
    }

    public LastPwException(Throwable throwable){
        super(throwable);
    }

    public LastPwException(String message, Throwable throwable){
        super(message, throwable);
    }
}
