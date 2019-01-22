package com.ashideas.httplibrary.request;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HttpRequest implements Serializable {

    public static final int FLAG_FORCE = 0b1;
    public static final int FLAG_CACHE_IN_MEMORY = 0b10;
    public static final int FLAG_SEGREGATE = 0b100;

    private String path;
    private Map<String, String> params;
    private Map<String, String> headers;
    private int cacheFlags;

    public HttpRequest(String path) {
        this.path = path;
        params = new HashMap<>();
        headers = new HashMap<>();
        cacheFlags = FLAG_CACHE_IN_MEMORY;
    }

    public void setFlags(int flag) {
        this.cacheFlags |= flag;
    }

    public void clearFlags(int flags) {
        this.cacheFlags &= ~flags;
    }

    public boolean checkFlag(int flags) {
        return (this.cacheFlags & flags) != 0;
    }

    public void addParam(String name, boolean value) {
        addParam(name, Boolean.toString(value));
    }

    public void addParam(String name, int value) {
        addParam(name, Integer.toString(value));
    }

    public void addParam(String name, float value) {
        addParam(name, Float.toString(value));
    }

    public void addParam(String name, double value) {
        addParam(name, Double.toString(value));
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String buildRawUrl() {
        if (params.isEmpty()) {
            return path;
        }

        StringBuilder urlBuilder = new StringBuilder(path + "?");
        List<String> keysList = new ArrayList<>(params.keySet()); // To be 100% predictable
        Collections.sort(keysList);
        for (String paramName : keysList) {
            urlBuilder.append(paramName).append("=").append(params.get(paramName)).append("&");
        }

        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // delete the last & or ?
        return urlBuilder.toString();
    }

    public String buildUrl() {
        return Uri.encode(buildRawUrl(), "/:&=?");
    }

    public abstract String getType();
}