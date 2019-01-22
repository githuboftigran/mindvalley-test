package com.ashideas.httplibrary.response;

import com.ashideas.httplibrary.client.HttpError;

public abstract class AbstractHttpCallback<T> implements HttpCallback<T> {

    @Override
    public void onError(HttpError error) {

    }

    @Override
    public void onSuccess(HttpResponse<T> response) {

    }

    @Override
    public void onSuccessInBackground(HttpResponse<T> response) {

    }

    @Override
    public void onDownloadProgress(long chunkIndex, long chunkCount) {

    }

    @Override
    public void onUploadProgress(long chunkIndex, long chunkCount) {

    }

    @Override
    public void onCancel() {

    }
}
