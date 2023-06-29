package com.backendabstractmodel.demo.enumeration;

public enum MessageEnum {

    UNEXPECTED_ERROR,
    ACCESS_DENIED,
    KEYCLOAK_CREATE_USER_ERROR,

    CAN_NOT_INSERT_WITH_ID,
    CAN_NOT_UPDATE_WITHOUT_ID,
    SAVE_ERROR,
    DELETE_ERROR,
    EMAIL_SEND_ERROR,

    INVALID_URL,
    EXPIRED_URL,
    EMAIL_ALREADY_VERIFIED,

    NO_RESULT_FOUND,
    DELETE_ERROR_NO_RESULT_FOUND,
    MISSING_DATA_FOR_SAVE,
    INVALID_DATA_FOR_SAVE,

    ALREADY_EXISTS_REGISTER_WITH_SAME_USERNAME,
    ALREADY_EXISTS_REGISTER_WITH_SAME_NAME,
    ALREADY_EXISTS_REGISTER_WITH_SAME_ACRONYM,
    ALREADY_EXISTS_REGISTER_WITH_SAME_NAME_OR_ACRONYM,
    CAN_NOT_SAVE_MULTIPLE_REGISTERS_WITH_SAME_NAME,

}
