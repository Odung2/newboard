package com.wemade.newboard.exception;

public class TokenNotIssueException extends RuntimeException{

    public TokenNotIssueException(){}

    public TokenNotIssueException(String message){
        super(message);
    }

    public TokenNotIssueException(Throwable throwable){
        super(throwable);
    }

    public TokenNotIssueException(String message, Throwable throwable){
        super(message, throwable);
    }
}
