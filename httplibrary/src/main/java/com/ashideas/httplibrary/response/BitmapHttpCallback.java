package com.ashideas.httplibrary.response;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHttpCallback extends AbstractHttpCallback<Bitmap> {
    @Override
    public Bitmap convert(byte[] rawResponse) {
        return BitmapFactory.decodeByteArray(rawResponse, 0, rawResponse.length);
    }
}
