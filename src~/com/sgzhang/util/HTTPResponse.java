package com.sgzhang.util;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sgzhang on Aug 05, 2016.
 * E-mail szhan45@lsu.edu.
 */
public class HTTPResponse {
    private String version = "HTTP/1.1";
    private int responseCode = 200;
    private String responseReason = "OK";
    private Map<String, String> headers = new LinkedHashMap<String, String>();
    private byte[] content;

    public void addDefaultHeaders() {
        headers.put("Date", new Date().toString());
        headers.put("Server", "Java Web Server by sgzhang");
//        headers.put("Connection", "close");
        headers.put("Content-Length", Integer.toString(content.length));
    }

    public String getVersion() {
        return version;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseReason() {
        return responseReason;
    }

    public Map<String, String> getHeader() {
        return headers;
    }

    public byte[] getContent() {
        return content;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponseReason(String responseReason) {
        this.responseReason = responseReason;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }
}
