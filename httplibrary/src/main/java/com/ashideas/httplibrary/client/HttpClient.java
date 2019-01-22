package com.ashideas.httplibrary.client;

import android.os.Handler;
import android.util.Log;

import com.ashideas.httplibrary.cache.CacheManager;
import com.ashideas.httplibrary.cache.LruCacheManager;
import com.ashideas.httplibrary.request.HttpRequest;
import com.ashideas.httplibrary.request.UploadHttpRequest;
import com.ashideas.httplibrary.response.HttpCallback;
import com.ashideas.httplibrary.response.HttpResponse;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    private static final String LOG_TAG = "HttpClient";

    private ExecutorService executor;
    private Map<String, List<HttpCallback>> callbacks;
    private Map<String, Future> futures; // Future objects for canceling download tasks
    private HttpConnectionFactory connectionFactory;
    private Handler handler;
    private CacheManager cacheManager;
    private int downloadBufferSize;
    private int uploadBufferSize;

    public HttpClient(ExecutorService executor, HttpConnectionFactory connectionFactory, Handler handler, CacheManager cacheManager, int downloadBufferSize, int uploadBufferSize) {
        this.downloadBufferSize = downloadBufferSize;
        this.uploadBufferSize = uploadBufferSize;
        this.executor = executor;
        this.connectionFactory = connectionFactory;
        this.handler = handler;
        this.cacheManager = cacheManager;

        callbacks = new HashMap<>();
        futures = new HashMap<>();
    }

    /**
     * Downloads the resource with given url asynchronously.
     * Decoder and callback should have the same generic type so callback will be called for a resource object decoded by the decoder.
     *
     * @param request  URL address of resource
     * @param callback
     * @param <T>      Processed response type
     * @return Cancellation token
     */
    public synchronized <T> CancellationToken requestAsync(final HttpRequest request, final HttpCallback<T> callback) {
        if (request == null) {
            return null;
        }

        String url = request.buildUrl();
        if (url.length() == 0) {
            return null;
        }

        boolean isForce = request.checkFlag(HttpRequest.FLAG_FORCE);
        boolean isSegregate = request.checkFlag(HttpRequest.FLAG_SEGREGATE);

        // This string will be used as key for Callbacks and Future
        String key = isSegregate ? request.hashCode() + url : url;

        List<HttpCallback> urlCallbacks = callbacks.get(key);
        if (urlCallbacks == null || isForce) { // The first call for this url or forced request

            if (urlCallbacks == null) {
                urlCallbacks = new ArrayList<>();
                callbacks.put(key, urlCallbacks);
            }
            Future future = executor.submit(() -> {
                HttpResponse<byte[]> response = requestSync(request);
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                if (response != null) {
                    executeSuccessCallbacks(key, response);
                }
            });

            futures.put(key, future);
        } else {
            Log.i(LOG_TAG, "Data for this url is already downloading, adding callback: " + url);
        }
        urlCallbacks.add(callback);

        return new CancellationToken(key, callback);
    }

    /**
     * @param request
     */
    public HttpResponse<byte[]> requestSync(HttpRequest request) {

        String url = request.buildUrl();

        if (!request.checkFlag(HttpRequest.FLAG_FORCE)) {
            byte[] cachedData = cacheManager.getFromCache(url);
            if (cachedData != null) {
                return new HttpResponse<>(0, null, cachedData);
            }
        }

        String requestType = request.getType();

        // Output stream for data. Need to close it in finally block.
        // So it will be closed in finally block.
        ByteArrayOutputStream outputStream = null;
        HttpConnection urlConnection = null;
        try {
            urlConnection = connectionFactory.create(url);
            urlConnection.connect();
            urlConnection.setRequestType(requestType);

            if (request instanceof UploadHttpRequest) { // Upload data
                boolean wasNotInterrupted = upload((UploadHttpRequest) request, url, urlConnection);
                if (!wasNotInterrupted) {
                    return null;
                }
            }

            int responseCode = urlConnection.getResponseCode();
            InputStream responseStream = urlConnection.getInputStream();

            Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();

            // Try to get data length
            List<String> contentLengthList = responseHeaders.get("Content-Length");

            long contentLength = -1;
            if (contentLengthList != null && !contentLengthList.isEmpty()) {
                try {
                    contentLength = Long.parseLong(contentLengthList.get(0));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            // Buffer size for chunks
            byte[] buffer = new byte[downloadBufferSize];
            int readBytes;

            outputStream = new ByteArrayOutputStream();

            long index = 0;
            final long chunksCount = contentLength == -1 ? -1 : (contentLength - 1) / downloadBufferSize + 1;
            while ((readBytes = responseStream.read(buffer)) > 0) {
                if (Thread.currentThread().isInterrupted()) { // Check for interruptions before every chunk
                    return null;
                }

                outputStream.write(buffer, 0, readBytes);
                final long currentIndex = index++;
                executeDownloadProgressCallbacks(url, currentIndex, chunksCount);
            }

            byte[] rawData = outputStream.toByteArray();
            if (request.checkFlag(HttpRequest.FLAG_CACHE_IN_MEMORY)) {
                cacheManager.putInCache(url, rawData);
            }

            return new HttpResponse<>(responseCode, responseHeaders, rawData);
        } catch (ProtocolException e) {
            e.printStackTrace();
            executeErrorCallbacks(url, HttpError.BAD_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            executeErrorCallbacks(url, HttpError.BAD_URL);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            executeErrorCallbacks(url, HttpError.FILE_NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
            executeErrorCallbacks(url, HttpError.NO_CONNECTION);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * Cancels download if source was the only one waiting.
     * Only removes callback otherwise.
     *
     * @param token Cancellation token
     */
    private synchronized void cancel(CancellationToken token) {
        if (token == null) {
            return;
        }
        String key = token.key;
        List<HttpCallback> urlCallbacks = callbacks.get(key);
        if (urlCallbacks == null) { // Should never happen, but who knows...
            return;
        }
        urlCallbacks.remove(token.callback);
        if (token.callback != null) {
            token.callback.onCancel();
        }
        if (!urlCallbacks.isEmpty()) { // Other callbacks are still waiting
            return;
        }
        // Remove callback list for this url
        callbacks.remove(key);
        Future future = futures.get(key);
        // Do nothing if the task was canceled after finishing, but handler didn't call executeCallbacksFor method yet.
        if (!future.isDone()) {
            future.cancel(true);
        }
        futures.remove(key);

        Log.i(LOG_TAG, "Download canceled: " + token.key);
    }

    /**
     * Uploads data from stream provided by request
     *
     * @param request
     * @return true if thread was not interrupted and false otherwise.
     */
    private boolean upload(UploadHttpRequest request, String url, HttpConnection urlConnection) throws IOException {
        InputStream contentInputStream = request.getInputStream();
        if (Thread.currentThread().isInterrupted()) {
            return false;
        }
        if (contentInputStream != null) {
            Log.i(LOG_TAG, "Uploading data: " + request.getContentDescription());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(uploadBufferSize);
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            byte[] buffer = new byte[uploadBufferSize];
            int chunkIndex = 0;
            long chunkCount = 1 + (request.getContentLength() - 1) / buffer.length;
            int readBytes;
            while ((readBytes = contentInputStream.read(buffer)) > 0) {
                if (Thread.currentThread().isInterrupted()) {
                    return false;
                }
                executeUploadProgressCallbacks(url, chunkIndex++, chunkCount);
                out.write(buffer, 0, readBytes);
            }
            contentInputStream.close();
            out.close();
        }

        return true;
    }

    private synchronized void executeDownloadProgressCallbacks(String url, long chunkIndex, long chunksCount) {
        List<HttpCallback> urlCallbacks = callbacks.get(url);
        if (urlCallbacks == null) { // Should never happen
            return;
        }

        for (HttpCallback callback : urlCallbacks) {
            if (callback != null) {
                handler.post(() -> callback.onDownloadProgress(chunkIndex, chunksCount));
            }
        }
    }

    private synchronized void executeUploadProgressCallbacks(String url, long chunkIndex, long chunksCount) {
        List<HttpCallback> urlCallbacks = callbacks.get(url);
        if (urlCallbacks == null) { // Should never happen
            return;
        }

        for (HttpCallback callback : urlCallbacks) {
            if (callback != null) {
                handler.post(() -> callback.onUploadProgress(chunkIndex, chunksCount));
            }
        }
    }

    private synchronized void executeSuccessCallbacks(String url, HttpResponse<byte[]> response) {
        List<HttpCallback> urlCallbacks = callbacks.get(url);
        if (urlCallbacks == null) {  // Should never happen
            return;
        }

        for (HttpCallback callback : urlCallbacks) {
            if (callback != null) {
                Object converted = callback.convert(response.getData());
                if (converted == null) {
                    handler.post(() -> callback.onError(HttpError.BAD_RESPONSE));
                } else {
                    HttpResponse responseForCallback = new HttpResponse<>(response.getResponseCode(), response.getHeaders(), converted);
                    callback.onSuccessInBackground(responseForCallback);
                    handler.post(() -> callback.onSuccess(responseForCallback));
                }
            }
        }

        callbacks.remove(url);
        futures.remove(url);
    }

    private synchronized void executeErrorCallbacks(String url, final HttpError error) {
        List<HttpCallback> urlCallbacks = callbacks.get(url);
        if (urlCallbacks == null) {
            return;
        }

        for (final HttpCallback callback : urlCallbacks) {
            if (callback != null) {
                handler.post(() -> callback.onError(error));
            }
        }

        callbacks.remove(url);
        futures.remove(url);
    }

    public static class Builder {

        private HttpClientOptions options;
        private ExecutorService executor;
        private HttpConnectionFactory connectionFactory;
        private CacheManager cacheManager;
        private Handler handler;

        public Builder() {
        }

        public Builder setOptions(HttpClientOptions options) {
            this.options = options;
            return this;
        }

        public Builder setExecutor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public Builder setConnectionFactory(HttpConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        public Builder setCacheManager(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
            return this;
        }

        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public HttpClient build() {
            options = options != null ? options : new HttpClientOptions.Builder().build();
            if (executor == null) {
                executor = new ThreadPoolExecutor(
                        options.getCorePoolSize(),
                        options.getCorePoolSize(),
                        options.getDefaultRequestTimeout(),
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>());
            }
            if (handler == null) {
                handler = new Handler();
            }
            if (connectionFactory == null) {
                connectionFactory = new UrlHttpConnectionFactory();
            }
            if (cacheManager == null) {
                cacheManager = new LruCacheManager();
            }
            return new HttpClient(executor, connectionFactory, handler, cacheManager, options.getDownloadBufferSize(), options.getUploadBufferSize());
        }
    }

    /**
     * A class for canceling downloads.
     * Encapsulates the key and the callback object.
     */
    public class CancellationToken {

        private String key;
        private HttpCallback callback;
        private boolean expired;

        private CancellationToken(String key, HttpCallback callback) {
            this.key = key;
            this.callback = callback;
            expired = false;
        }

        public void cancel() {
            if (expired) {
                Log.e(LOG_TAG, "Cancelled already expired token.");
                return;
            }
            expired = true;
            HttpClient.this.cancel(this);
        }
    }
}