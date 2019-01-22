package com.ashideas.httplibrary.client;

public class HttpClientOptions {

    private int corePoolSize;
    private int maximumPoolSize;
    private long defaultRequestTimeout;
    private int downloadBufferSize;
    private int uploadBufferSize;

    private HttpClientOptions() {
        maximumPoolSize = corePoolSize = Runtime.getRuntime().availableProcessors();
        defaultRequestTimeout = 10 * 1000; // 10 seconds
        uploadBufferSize = downloadBufferSize = 8 * 1024; // 8Kbytes
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public long getDefaultRequestTimeout() {
        return defaultRequestTimeout;
    }

    public int getDownloadBufferSize() {
        return downloadBufferSize;
    }

    public int getUploadBufferSize() {
        return uploadBufferSize;
    }

    public static class Builder {
        private int corePoolSize;
        private int maximumPoolSize;
        private long requestTimeout;
        private int downloadBufferSize;
        private int uploadBufferSize;

        public Builder() {
            maximumPoolSize = corePoolSize = Runtime.getRuntime().availableProcessors();
            requestTimeout = 10 * 1000; // 10 seconds
            uploadBufferSize = downloadBufferSize = 8 * 1024; // 8Kbytes
        }

        public Builder setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder setRequestTimeout(long requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder setDownloadBufferSize(int downloadBufferSize) {
            this.downloadBufferSize = downloadBufferSize;
            return this;
        }

        public Builder setUploadBufferSize(int uploadBufferSize) {
            this.uploadBufferSize = uploadBufferSize;
            return this;
        }

        public HttpClientOptions build() {
            HttpClientOptions options = new HttpClientOptions();
            options.corePoolSize = corePoolSize;
            options.maximumPoolSize = maximumPoolSize;
            options.defaultRequestTimeout = requestTimeout;
            options.downloadBufferSize = downloadBufferSize;
            options.uploadBufferSize = uploadBufferSize;
            return options;
        }
    }
}