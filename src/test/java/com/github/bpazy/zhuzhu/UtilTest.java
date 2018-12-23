package com.github.bpazy.zhuzhu;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void testExtractUrls() {
        String baseDomain = "http://example.com";
        String html = "<html>{}</html>";
        String a1 = "<a href=\"a1.html\"></a>";
        String a2 = "<a href=\"/a2.html\"></a>";
        String a3 = "<a href=\"" + baseDomain + "/a3.html" + "\"></a>";
        assertEquals("http://example.com/a1.html", Util.extractUrls(baseDomain, html.replace("{}", a1).getBytes(), "UTF8").get(0));
        assertEquals("http://example.com/a2.html", Util.extractUrls(baseDomain, html.replace("{}", a2).getBytes(), "UTF8").get(0));
        assertEquals("http://example.com/a3.html", Util.extractUrls(baseDomain, html.replace("{}", a3).getBytes(), "UTF8").get(0));
        assertEquals(0, Util.extractUrls(null, html.replace("{}", a3).getBytes(), "UTF8").size());
        assertEquals(0, Util.extractUrls("", html.replace("{}", a3).getBytes(), "UTF8").size());
    }

    @Test
    public void testDecodeUrl() {
        assertEquals("http://example.com/login?login=login", Util.normalizeUrl("http://example.com/login?login=login"));
        assertEquals("http://example.com/login?login=login###", Util.normalizeUrl("http://example.com/login?login=login###"));
        assertEquals("http://example.com/login?login=login#/123", Util.normalizeUrl("http://example.com/login?login=login#/123"));
        assertEquals("http://example.com/login?login=login%2B123", Util.normalizeUrl("http://example.com/login?login=login+123"));
        assertEquals("http://example.com/login?login=login%3F123", Util.normalizeUrl("http://example.com/login?login=login?123"));
    }
}