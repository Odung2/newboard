package com.wemade.newboard.exception;

public class LoginLockException extends RuntimeException{
    public LoginLockException(){}

    public LoginLockException(String message){
        super(message);
    }

    public LoginLockException(Throwable throwable){
        super(throwable);
    }

    public LoginLockException(String message, Throwable throwable){
        super(message, throwable);
    }
}
