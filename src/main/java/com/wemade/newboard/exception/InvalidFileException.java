package com.wemade.newboard.exception;

public class InvalidFileException extends RuntimeException{
    public InvalidFileException(){}

    public InvalidFileException(String message){
        super(message);
    }

    public InvalidFileException(Throwable throwable){
        super(throwable);
    }

    public InvalidFileException(String message, Throwable throwable){
        super(message, throwable);
    }
}
