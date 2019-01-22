package com.ashideas.httplibrary.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse<T> {

    private int responseCode;
    private Map<String, List<String>> headers;
    private T data;

    public HttpResponse(int responseCode, Map<String, List<String>> headers, T data) {
        this.responseCode = responseCode;
        this.headers = headers == null ? null : new HashMap<>(headers);
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public T getData() {
        return data;
    }
}