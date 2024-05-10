package com.wemade.newboard.exception;


public class PasswordRegexException extends RuntimeException{

    public PasswordRegexException(){}

    public PasswordRegexException(String message){
        super(message);
    }

    public PasswordRegexException(Throwable throwable){
        super(throwable);
    }

    public PasswordRegexException(String message, Throwable throwable){
        super(message, throwable);
    }
}
