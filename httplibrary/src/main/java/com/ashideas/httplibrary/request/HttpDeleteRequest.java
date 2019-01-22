package com.ashideas.httplibrary.request;

public class HttpDeleteRequest extends HttpRequest {

    public HttpDeleteRequest(String url) {
        super(url);
        setFlags(FLAG_FORCE | FLAG_SEGREGATE);
        clearFlags(FLAG_CACHE_IN_MEMORY);
    }

    @Override
    public final String getType() {
        return "DELETE";
    }
}