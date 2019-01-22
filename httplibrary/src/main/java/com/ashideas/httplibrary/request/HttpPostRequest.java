package com.ashideas.httplibrary.request;

public abstract class HttpPostRequest extends UploadHttpRequest {

    public HttpPostRequest(String url) {
        super(url);
    }

    @Override
    public final String getType() {
        return "POST";
    }
}