package ru.trader.kairos.error;

import graphql.ErrorClassification;

public enum ErrorType implements ErrorClassification {
    NOT_FOUND,
    FORBIDDEN,
    BAD_REQUEST,
    UNAUTHORIZED,
    INTERNAL_ERROR
}
