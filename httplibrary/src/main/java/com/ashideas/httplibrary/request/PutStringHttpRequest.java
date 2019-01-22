package com.ashideas.httplibrary.request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PutStringHttpRequest extends HttpPutRequest {

    private String body;

    public PutStringHttpRequest(String url, String body) {
        super(url);
        this.body = body;
    }

    @Override
    public long getContentLength() {
        return body.getBytes().length;
    }

    @Override
    public String getContentDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(body.getBytes());
    }
}
