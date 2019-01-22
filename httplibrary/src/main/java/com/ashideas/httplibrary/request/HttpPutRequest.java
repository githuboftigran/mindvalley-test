package com.ashideas.httplibrary.request;

public abstract class HttpPutRequest extends UploadHttpRequest {

    public HttpPutRequest(String url) {
        super(url);
    }

    @Override
    public final String getType() {
        return "PUT";
    }
}