package com.github.bpazy.zhuzhu;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void testDecodeUrl() {
        assertEquals("http://example.com/login?login=login", Util.normalizeUrl("http://example.com/login?login=login"));
        assertEquals("http://example.com/login?login=login###", Util.normalizeUrl("http://example.com/login?login=login###"));
        assertEquals("http://example.com/login?login=login#/123", Util.normalizeUrl("http://example.com/login?login=login#/123"));
        assertEquals("http://example.com/login?login=login%2B123", Util.normalizeUrl("http://example.com/login?login=login+123"));
        assertEquals("http://example.com/login?login=login%3F123", Util.normalizeUrl("http://example.com/login?login=login?123"));
    }

    @Test
    public void testGetDomain() {
        assertEquals("http://www.example.com", Util.getDomain("http://www.example.com/abc"));
        assertEquals("http://www.example.com", Util.getDomain("http://www.example.com?a=a"));
    }
}