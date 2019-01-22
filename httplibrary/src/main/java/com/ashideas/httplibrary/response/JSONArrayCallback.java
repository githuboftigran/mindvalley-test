package com.ashideas.httplibrary.response;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONArrayCallback extends AbstractHttpCallback<JSONArray> {
    @Override
    public JSONArray convert(byte[] rawResponse) {
        String rawString = new String(rawResponse);
        try {
            return new JSONArray(rawString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
