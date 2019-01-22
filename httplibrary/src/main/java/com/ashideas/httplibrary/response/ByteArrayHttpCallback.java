package com.ashideas.httplibrary.response;

public class ByteArrayHttpCallback extends AbstractHttpCallback<byte[]> {
    @Override
    public byte[] convert(byte[] rawResponse) {
        return rawResponse;
    }
}
