package com.ashideas.httplibrary.client;

public enum HttpError {
    NO_CONNECTION("No internet connection or requesting host was not found. Please check your internet connection."),
    BAD_URL("Requesting url is invalid."),
    BAD_RESPONSE("Bad response"),
    FILE_NOT_FOUND("File not found");

    private String errorMessage;
    HttpError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
