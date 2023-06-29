package com.backendabstractmodel.demo.exceptions;

import com.backendabstractmodel.demo.enumeration.MessageEnum;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String format, Object... args) {
        super(String.format(format, args));
    }

    public BusinessException(MessageEnum messageEnum) {
        super(messageEnum.toString());
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
