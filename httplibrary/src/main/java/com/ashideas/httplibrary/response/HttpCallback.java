package com.ashideas.httplibrary.response;

import com.ashideas.httplibrary.client.HttpError;

/**
 * A callback for HttpClient requests.
 * @param <T> type of converted response data.
 */
public interface HttpCallback<T> {

    /**
     * Converts raw response bytes to data with appropriate type T
     * @param rawResponse
     * @return
     */
    T convert(byte[] rawResponse);

    void onError(HttpError error);

    void onSuccess(HttpResponse<T> response);

    /**
     * Called in background thread.
     * All additional long-time processing of response data can be done in this method.
     * @param response
     */
    void onSuccessInBackground(HttpResponse<T> response);

    void onDownloadProgress(long chunkIndex, long chunkCount);

    void onUploadProgress(long chunkIndex, long chunkCount);

    void onCancel();
}
