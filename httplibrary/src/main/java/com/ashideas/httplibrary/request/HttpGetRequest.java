package com.ashideas.httplibrary.request;

public class HttpGetRequest extends HttpRequest {

    public HttpGetRequest(String url) {
        super(url);
    }

    @Override
    public final String getType() {
        return "GET";
    }
}