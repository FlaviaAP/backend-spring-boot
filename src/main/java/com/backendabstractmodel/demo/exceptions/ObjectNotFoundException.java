package com.backendabstractmodel.demo.exceptions;

import com.backendabstractmodel.demo.enumeration.MessageEnum;

public class ObjectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ObjectNotFoundException() {
        super(MessageEnum.NO_RESULT_FOUND.toString());
    }

    public ObjectNotFoundException(MessageEnum messageEnum) {
        super(messageEnum.toString());
    }

    public ObjectNotFoundException(MessageEnum messageEnum, Throwable cause) {
        super(messageEnum.toString(), cause);
    }

    public ObjectNotFoundException(String msg) {
        super(msg);
    }

    public ObjectNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
