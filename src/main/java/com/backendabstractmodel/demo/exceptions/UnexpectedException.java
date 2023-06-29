package com.backendabstractmodel.demo.exceptions;

import com.backendabstractmodel.demo.enumeration.MessageEnum;

public class UnexpectedException extends RuntimeException {

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedException(MessageEnum messageEnum, Throwable cause) {
        super(messageEnum.toString(), cause);
    }

    public UnexpectedException(Throwable cause) {
        super(MessageEnum.UNEXPECTED_ERROR.toString(), cause);
    }
}
