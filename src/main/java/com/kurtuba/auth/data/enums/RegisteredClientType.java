package com.kurtuba.auth.data.enums;

public enum RegisteredClientType {
    DEFAULT,// same as generic but a fallback in case of no client specified
    MOBILE, // for mobile clients
    WEB, // for web clients
    SERVICE,// for service clients
    GENERIC // for any client
}
