package com.ashideas.httplibrary;

import com.ashideas.httplibrary.request.HttpRequest;

import org.junit.Assert;
import org.junit.Test;

public class HttpRequestTest {
    @Test
    public void buildUrl_isCorrect() {
        HttpRequest request = new HttpRequest("https://www.youtube.com/watch") {
            @Override
            public String getType() {
                return null;
            }
        };
        request.addParam("v", "ogfYd705cRs");
        request.addParam("feature", "youtu.be");
        request.addParam("t", "6");
        request.addParam("param2", "val+2");
        Assert.assertEquals(request.buildRawUrl(), "https://www.youtube.com/watch?feature=youtu.be&param2=val+2&t=6&v=ogfYd705cRs");
    }
}
