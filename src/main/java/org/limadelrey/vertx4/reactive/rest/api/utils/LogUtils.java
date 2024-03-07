package org.limadelrey.vertx4.reactive.rest.api.utils;

public enum LogUtils {

    REGULAR_CALL_SUCCESS_MESSAGE("%s called  success - %s"),
    REGULAR_CALL_ERROR_MESSAGE("%s called  error - %s"),
    NO_ENTITY_WITH_ID_MESSAGE("No entity with id %d"),


    NO_ENTITY_WITH_ATTR_MESSAGE("No entity with attr %s"),

    CANNOT_CREATE_ENTITY_MESSAGE("Cannot create a new entity"),
    RUN_HTTP_SERVER_SUCCESS_MESSAGE("HTTP server running on port %s"),

    RUN_HTTP_PAGES_SERVER_SUCCESS_MESSAGE("HTTP pages server running on port %s"),

    RUN_HTTP_SERVER_ERROR_MESSAGE("Cannot run HTTP server"),

    RUN_HTTP_PAGES_SERVER_ERROR_MESSAGE("Cannot run HTTP pages server"),

    NULL_OFFSET_ERROR_MESSAGE("Offset can't be null. Page %s and limit %s"),
    RUN_APP_SUCCESSFULLY_MESSAGE("vertx-4-reactive-rest-api started successfully in %d ms");

    private final String message;

    LogUtils(final String message) {
        this.message = message;
    }

    public String buildMessage(Object... argument) {
        return String.format(message, argument);
    }

}
