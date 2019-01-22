package com.ashideas.httplibrary.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class UrlHttpConnection implements HttpConnection {

    private URL url;
    private HttpURLConnection connection;

    public UrlHttpConnection(String urlStr) throws MalformedURLException {
        url = new URL(urlStr);
    }

    @Override
    public void connect() throws IOException {
        connection = (HttpURLConnection) url.openConnection();
    }

    @Override
    public void setRequestType(String type) throws ProtocolException {
        connection.setRequestMethod(type);
    }

    @Override
    public void setRequestProperty(String key, String value) {
        connection.setRequestProperty(key, value);
    }

    @Override
    public void setDoInput(boolean doInput) {
        connection.setDoInput(doInput);
    }

    @Override
    public void setDoOutput(boolean doOutput) {
        connection.setDoOutput(doOutput);
    }

    @Override
    public void setChunkedStreamingMode(int chunkLength) {
        connection.setChunkedStreamingMode(chunkLength);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return connection.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    @Override
    public int getResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return connection.getHeaderFields();
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }
}
