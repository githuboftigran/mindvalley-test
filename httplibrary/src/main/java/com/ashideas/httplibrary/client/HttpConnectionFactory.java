package com.ashideas.httplibrary.client;

import java.net.MalformedURLException;

public interface HttpConnectionFactory {
    HttpConnection create(String url) throws MalformedURLException;
}