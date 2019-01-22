package com.ashideas.httplibrary;

import android.os.Handler;
import android.os.Looper;

import com.ashideas.httplibrary.cache.CacheManager;
import com.ashideas.httplibrary.client.HttpClient;
import com.ashideas.httplibrary.client.HttpClientOptions;
import com.ashideas.httplibrary.client.HttpConnection;
import com.ashideas.httplibrary.client.HttpConnectionFactory;
import com.ashideas.httplibrary.client.HttpError;
import com.ashideas.httplibrary.request.HttpGetRequest;
import com.ashideas.httplibrary.request.HttpPostRequest;
import com.ashideas.httplibrary.request.HttpRequest;
import com.ashideas.httplibrary.response.ByteArrayHttpCallback;
import com.ashideas.httplibrary.response.HttpResponse;
import com.ashideas.httplibrary.response.JSONObjectCallback;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class HttpClientTest {

    private static final String GET_URL = "https://path";
    private static final String JSON = "{\"int0\":1000,\"int1\":1001,\"int2\":1002,\"int3\":1003,\"string0\":\"large string 0\",\"string1\":\"large string 1\",\"string2\":\"large string 2\",\"string3\":\"large string 3\"}";
    private static final String BAD_JSON = "bad json";
    private static final int BUFFER_SIZE = 16;

    private static InputStream mock100DelayInputStream() throws IOException {
        InputStream mockedStream = mock(InputStream.class);
        when(mockedStream.read(any(byte[].class))).thenAnswer(new Answer<Integer>() {

            private int count = JSON.length();

            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(100);
                count -= BUFFER_SIZE;
                return count <= 0 ? 0 : BUFFER_SIZE;
            }
        });
        return mockedStream;
    }

    private static HttpConnectionFactory mock100DelayedJsonHttpConnectionFactory() throws IOException {
        HttpConnection mockedHttpConnection = mock(HttpConnection.class);
        when(mockedHttpConnection.getHeaderFields()).thenReturn(new HashMap<>());
        InputStream delay100Stream = mock100DelayInputStream();
        when(mockedHttpConnection.getInputStream()).thenReturn(delay100Stream);
        when(mockedHttpConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(mockedHttpConnection.getResponseCode()).thenReturn(200);
        return url -> mockedHttpConnection;
    }

    private static HttpConnectionFactory mockNoHttpConnectionFactory() throws IOException {
        HttpConnection mockedHttpConnection = mock(HttpConnection.class);
        when(mockedHttpConnection.getInputStream()).thenThrow(new IOException());
        return url -> mockedHttpConnection;
    }

    private static HttpConnectionFactory mockBadJsonConnectionFactory() throws IOException {
        HttpConnection mockedHttpConnection = mock(HttpConnection.class);
        when(mockedHttpConnection.getHeaderFields()).thenReturn(new HashMap<>());
        when(mockedHttpConnection.getInputStream()).thenReturn(new ByteArrayInputStream(BAD_JSON.getBytes()));
        when(mockedHttpConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(mockedHttpConnection.getResponseCode()).thenReturn(200);
        return url -> mockedHttpConnection;
    }

    private static HttpConnectionFactory mockBadUrlHttpConnectionFactory() throws IOException {
        HttpConnectionFactory mockedHttpConnection = mock(HttpConnectionFactory.class);

        when(mockedHttpConnection.create(anyString())).thenThrow(new MalformedURLException());
        return mockedHttpConnection;
    }

    private static HttpConnectionFactory mockJsonHttpConnectionFactory() throws IOException {
        HttpConnection mockedHttpConnection = mock(HttpConnection.class);
        when(mockedHttpConnection.getHeaderFields()).thenReturn(new HashMap<>());
        when(mockedHttpConnection.getInputStream()).thenReturn(new ByteArrayInputStream(JSON.getBytes()));
        when(mockedHttpConnection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(mockedHttpConnection.getResponseCode()).thenReturn(200);
        return url -> mockedHttpConnection;
    }

    private static HttpRequest mockGetRequest() {
        HttpRequest request = mock(HttpGetRequest.class);
        when(request.buildUrl()).thenReturn(GET_URL);
        return request;
    }

    private static HttpRequest mockPostRequest() throws IOException {
        HttpPostRequest request = mock(HttpPostRequest.class);
        when(request.buildUrl()).thenReturn(GET_URL);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(JSON.getBytes());
        when(request.getInputStream()).thenReturn(inputStream);
        when(request.getContentLength()).thenReturn((long) JSON.getBytes().length);
        return request;
    }

    private static HttpRequest mockNoFilePostRequest() throws IOException {
        HttpPostRequest request = mock(HttpPostRequest.class);
        when(request.buildUrl()).thenReturn(GET_URL);
        when(request.getInputStream()).thenThrow(new FileNotFoundException());
        when(request.getContentLength()).thenReturn((long) JSON.getBytes().length);
        return request;
    }

    private static HttpClient createClientWithMocked_Cache_HttpConnectionAnd_16ByteBuffers() throws IOException {
        CacheManager mockedCacheManager = mock(CacheManager.class);
        HttpConnectionFactory mockedConnectionFactory = mockJsonHttpConnectionFactory();
        HttpClientOptions options = new HttpClientOptions.Builder()
                .setDownloadBufferSize(BUFFER_SIZE)
                .setUploadBufferSize(BUFFER_SIZE).build();

        return new HttpClient.Builder()
                .setOptions(options)
                .setCacheManager(mockedCacheManager)
                .setConnectionFactory(mockedConnectionFactory)
                .setHandler(new Handler(Looper.getMainLooper()))
                .build();
    }

    private static HttpClient createClientWithMocked_Cache_NoHttpConnectionAnd_16ByteBuffers() throws IOException {
        CacheManager mockedCacheManager = mock(CacheManager.class);
        HttpConnectionFactory mockedConnectionFactory = mockNoHttpConnectionFactory();
        HttpClientOptions options = new HttpClientOptions.Builder()
                .setDownloadBufferSize(BUFFER_SIZE)
                .setUploadBufferSize(BUFFER_SIZE).build();

        return new HttpClient.Builder()
                .setOptions(options)
                .setCacheManager(mockedCacheManager)
                .setConnectionFactory(mockedConnectionFactory)
                .setHandler(new Handler(Looper.getMainLooper()))
                .build();
    }

    private static HttpClient createClientWithMocked_Cache_MalformedURLHttpConnectionAnd_16ByteBuffers() throws IOException {
        CacheManager mockedCacheManager = mock(CacheManager.class);
        HttpConnectionFactory mockedConnectionFactory = mockBadUrlHttpConnectionFactory();
        HttpClientOptions options = new HttpClientOptions.Builder()
                .setDownloadBufferSize(BUFFER_SIZE)
                .setUploadBufferSize(BUFFER_SIZE).build();

        return new HttpClient.Builder()
                .setOptions(options)
                .setCacheManager(mockedCacheManager)
                .setConnectionFactory(mockedConnectionFactory)
                .setHandler(new Handler(Looper.getMainLooper()))
                .build();
    }

    private static HttpClient createClientWithMocked_Cache_HttpConnection_16ByteBuffers_100DelayInputStream() throws IOException {
        CacheManager mockedCacheManager = mock(CacheManager.class);
        HttpConnectionFactory mockedConnectionFactory = mock100DelayedJsonHttpConnectionFactory();
        HttpClientOptions options = new HttpClientOptions.Builder()
                .setDownloadBufferSize(BUFFER_SIZE)
                .setUploadBufferSize(BUFFER_SIZE).build();

        return new HttpClient.Builder()
                .setOptions(options)
                .setCacheManager(mockedCacheManager)
                .setConnectionFactory(mockedConnectionFactory)
                .setHandler(new Handler(Looper.getMainLooper()))
                .build();
    }

    private static HttpClient createClientWithMocked_Cache_BadJsonHttpConnection_16ByteBuffers() throws IOException {
        CacheManager mockedCacheManager = mock(CacheManager.class);
        HttpConnectionFactory mockedConnectionFactory = mockBadJsonConnectionFactory();
        HttpClientOptions options = new HttpClientOptions.Builder()
                .setDownloadBufferSize(BUFFER_SIZE)
                .setUploadBufferSize(BUFFER_SIZE).build();

        return new HttpClient.Builder()
                .setOptions(options)
                .setCacheManager(mockedCacheManager)
                .setConnectionFactory(mockedConnectionFactory)
                .setHandler(new Handler(Looper.getMainLooper()))
                .build();
    }

    @Test
    public void ifFoundInCache_doesntRequest() {

        CacheManager mockedCacheManager = mock(CacheManager.class);
        byte[] cachedData = (new byte[]{1, 2, 3, 4});
        when(mockedCacheManager.getFromCache(GET_URL)).thenReturn(cachedData);
        HttpRequest mockedRequest = mockGetRequest();
        HttpClient client = new HttpClient.Builder().setCacheManager(mockedCacheManager).setHandler(new Handler(Looper.getMainLooper())).build();

        HttpResponse<byte[]> response = client.requestSync(mockedRequest);
        Assert.assertEquals(response.getResponseCode(), 0);
        Assert.assertArrayEquals(response.getData(), cachedData);
    }

    @Test
    public void ifNotFoundInCache_requests() throws IOException {

        HttpClient client = createClientWithMocked_Cache_HttpConnectionAnd_16ByteBuffers();
        HttpRequest mockedRequest = mockGetRequest();

        HttpResponse<byte[]> response = client.requestSync(mockedRequest);
        Assert.assertEquals(response.getResponseCode(), 200);
        Assert.assertArrayEquals(response.getData(), JSON.getBytes());
    }

    @Test
    public void postsProgress_whileDownloading() throws IOException, InterruptedException {
        CountDownLatch countDown = new CountDownLatch(1);

        HttpRequest mockedRequest = mockGetRequest();
        HttpClient client = createClientWithMocked_Cache_HttpConnectionAnd_16ByteBuffers();

        StringBuilder builder = new StringBuilder();
        client.requestAsync(mockedRequest, new ByteArrayHttpCallback() {

            @Override
            public void onDownloadProgress(long chunkIndex, long chunkCount) {
                builder.append(chunkIndex).append('/').append(chunkCount).append(',');
            }

            @Override
            public void onSuccessInBackground(HttpResponse<byte[]> response) {
                countDown.countDown();
            }
        });

        countDown.await();
        String resultString = builder.toString();
        builder.delete(0, resultString.length());
        for (int i = 0; i < 10; i++) { // 157 characters, buffer size: 16, should be 10 chunks
            builder.append(i).append('/').append(-1).append(',');
        }

        Assert.assertEquals(resultString, builder.toString());
    }

    @Test
    public void postsProgress_whileUploading() throws IOException, InterruptedException {

        CountDownLatch countDown = new CountDownLatch(1);

        HttpRequest mockedRequest = mockPostRequest();
        HttpClient client = createClientWithMocked_Cache_HttpConnectionAnd_16ByteBuffers();

        StringBuilder builder = new StringBuilder();
        client.requestAsync(mockedRequest, new ByteArrayHttpCallback() {

            @Override
            public void onUploadProgress(long chunkIndex, long chunkCount) {
                builder.append(chunkIndex).append('/').append(chunkCount).append(',');
            }

            @Override
            public void onSuccess(HttpResponse<byte[]> response) {
                countDown.countDown();
            }
        });

        countDown.await();
        String resultString = builder.toString();
        builder.delete(0, resultString.length());
        for (int i = 0; i < 10; i++) { // 157 characters, buffer size: 16, should be 10 chunks
            builder.append(i).append('/').append(10).append(',');
        }

        Assert.assertEquals(resultString, builder.toString());
    }

    @Test
    public void ifCancelled_callCancelAndNothingElse() throws IOException, InterruptedException {
        CountDownLatch countDown = new CountDownLatch(1);

        HttpClient client = createClientWithMocked_Cache_HttpConnection_16ByteBuffers_100DelayInputStream();
        HttpRequest request = mockGetRequest();

        List<String> status = new ArrayList<>();
        HttpClient.CancellationToken token = client.requestAsync(request, new ByteArrayHttpCallback() {

            @Override
            public void onSuccess(HttpResponse<byte[]> response) {
                status.add("success");
            }

            @Override
            public void onSuccessInBackground(HttpResponse<byte[]> response) {
                status.add("successInBackground");
            }

            @Override
            public void onError(HttpError error) {
                status.add("error");
            }

            @Override
            public void onCancel() {
                status.add("cancelled");
                countDown.countDown();
            }
        });
        Thread.sleep(500);
        token.cancel();
        countDown.await();
        Thread.sleep(100);
        Assert.assertEquals(status.size(), 1);
        Assert.assertEquals(status.get(0), "cancelled");
    }

    @Test
    public void ifCallRemain_doesntCancelRequest() throws IOException, InterruptedException {
        CountDownLatch countDown = new CountDownLatch(1);

        HttpClient client = createClientWithMocked_Cache_HttpConnection_16ByteBuffers_100DelayInputStream();
        HttpRequest request0 = mockGetRequest();
        HttpRequest request1 = mockGetRequest();

        List<Integer> status = new ArrayList<>();
        status.add(-1);
        HttpClient.CancellationToken token = client.requestAsync(request0, new ByteArrayHttpCallback());
        client.requestAsync(request1, new ByteArrayHttpCallback() {
            @Override
            public void onSuccess(HttpResponse<byte[]> response) {
                status.set(0, response.getResponseCode());
                countDown.countDown();
            }
        });

        Thread.sleep(500);
        token.cancel();
        countDown.await();
        Assert.assertEquals(status.get(0), new Integer(200));
    }

    @Test
    public void ifConnectionLost_errorNO_CONNECTIONAndNothingElse() throws IOException, InterruptedException {
        CountDownLatch countDown = new CountDownLatch(1);
        HttpClient client = createClientWithMocked_Cache_NoHttpConnectionAnd_16ByteBuffers();
        HttpRequest request = mockGetRequest();

        List<HttpError> httpError = new ArrayList<>();
        client.requestAsync(request, new ByteArrayHttpCallback() {
            @Override
            public void onSuccess(HttpResponse<byte[]> response) {
                httpError.add(null);
            }

            @Override
            public void onCancel() {
                httpError.add(null);
            }

            @Override
            public void onError(HttpError error) {
                httpError.add(error);
                countDown.countDown();
            }
        });

        countDown.await();
        Thread.sleep(100);
        Assert.assertEquals(httpError.size(), 1);
        Assert.assertEquals(httpError.get(0), HttpError.NO_CONNECTION);
    }

    @Test
    public void ifBadUrl_returnsErrorBAD_URL() throws IOException, InterruptedException {
        CountDownLatch countDown = new CountDownLatch(1);
        HttpClient client = createClientWithMocked_Cache_MalformedURLHttpConnectionAnd_16ByteBuffers();
        HttpRequest request = mockGetRequest();

        List<HttpError> httpError = new ArrayList<>();
        client.requestAsync(request, new ByteArrayHttpCallback() {
            @Override
            public void onSuccess(HttpResponse<byte[]> response) {
                httpError.add(null);
            }

            @Override
            public void onCancel() {
                httpError.add(null);
            }

            @Override
            public void onError(HttpError error) {
                httpError.add(error);
                countDown.countDown();
            }
        });

        countDown.await();
        Thread.sleep(100);
        Assert.assertEquals(httpError.size(), 1);
        Assert.assertEquals(httpError.get(0), HttpError.BAD_URL);
    }

    @Test
    public void ifUploadFileNotFound_returnsErrorFILE_NOT_FOUND() throws IOException, InterruptedException {
        CountDownLatch countDown = new CountDownLatch(1);
        HttpClient client = createClientWithMocked_Cache_NoHttpConnectionAnd_16ByteBuffers();
        HttpRequest request = mockNoFilePostRequest();

        List<HttpError> httpError = new ArrayList<>();
        client.requestAsync(request, new ByteArrayHttpCallback() {
            @Override
            public void onSuccess(HttpResponse<byte[]> response) {
                httpError.add(null);
            }

            @Override
            public void onCancel() {
                httpError.add(null);
            }

            @Override
            public void onError(HttpError error) {
                httpError.add(error);
                countDown.countDown();
            }
        });

        countDown.await();
        Thread.sleep(100);
        Assert.assertEquals(httpError.size(), 1);
        Assert.assertEquals(httpError.get(0), HttpError.FILE_NOT_FOUND);
    }

    @Test
    public void ifBadResponse_returnsErrorBAD_RESPONSE() throws IOException, InterruptedException {
        CountDownLatch countDown = new CountDownLatch(1);
        HttpClient client = createClientWithMocked_Cache_BadJsonHttpConnection_16ByteBuffers();
        HttpRequest request = mockGetRequest();

        List<HttpError> httpError = new ArrayList<>();
        client.requestAsync(request, new JSONObjectCallback() {
            @Override
            public void onSuccess(HttpResponse<JSONObject> response) {
                httpError.add(null);
            }

            @Override
            public void onCancel() {
                httpError.add(null);
            }

            @Override
            public void onError(HttpError error) {
                httpError.add(error);
                countDown.countDown();
            }
        });

        countDown.await();
        Thread.sleep(100);
        Assert.assertEquals(httpError.size(), 1);
        Assert.assertEquals(httpError.get(0), HttpError.BAD_RESPONSE);
    }
}