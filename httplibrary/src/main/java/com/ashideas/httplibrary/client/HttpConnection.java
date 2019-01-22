package com.ashideas.httplibrary.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;

public interface HttpConnection {
    void connect() throws IOException;
    void disconnect();
    void setRequestType(String type) throws ProtocolException;
    void setRequestProperty(String key, String value);
    void setDoInput(boolean doInput);
    void setDoOutput(boolean doOutput);
    void setChunkedStreamingMode(int chunkLength);
    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;
    int getResponseCode() throws IOException;
    Map<String, List<String>> getHeaderFields();
}