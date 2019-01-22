package com.ashideas.httplibrary.response;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectCallback extends AbstractHttpCallback<JSONObject> {
    @Override
    public JSONObject convert(byte[] rawResponse) {
        String rawString = new String(rawResponse);
        try {
            return new JSONObject(rawString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
