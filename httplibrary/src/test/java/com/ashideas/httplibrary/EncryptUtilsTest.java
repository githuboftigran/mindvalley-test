package com.ashideas.httplibrary;

import org.junit.Assert;
import org.junit.Test;

public class EncryptUtilsTest {
    @Test
    public void SHA1_isCorrect() {
        Assert.assertEquals(EncryptUtils.sha1("I am your father"), "0D0E9DA0E668CD73155E7E4F2F702050FE45DF8A");
        Assert.assertEquals(EncryptUtils.sha1("42"), "92CFCEB39D57D914ED8B14D0E37643DE0797AE56");
    }
}