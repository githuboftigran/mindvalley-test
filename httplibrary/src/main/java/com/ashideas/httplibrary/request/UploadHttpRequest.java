package com.ashideas.httplibrary.request;

import java.io.IOException;
import java.io.InputStream;
public abstract class UploadHttpRequest extends HttpRequest {

    public UploadHttpRequest(String url) {
        super(url);
        setFlags(FLAG_FORCE | FLAG_SEGREGATE);
        clearFlags(FLAG_CACHE_IN_MEMORY);
    }

    public abstract InputStream getInputStream() throws IOException;

    public abstract long getContentLength();

    public abstract String getContentDescription();
}