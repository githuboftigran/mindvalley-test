package com.ashideas.httplibrary.client;

import java.net.MalformedURLException;

public class UrlHttpConnectionFactory implements HttpConnectionFactory {
    @Override
    public HttpConnection create(String url) throws MalformedURLException {
        return new UrlHttpConnection(url);
    }
}
