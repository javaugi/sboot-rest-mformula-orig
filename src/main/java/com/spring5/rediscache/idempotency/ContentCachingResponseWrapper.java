/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.rediscache.idempotency;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ContentCachingResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private PrintWriter writer;

    public ContentCachingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(content);
        }
        return writer;
    }

    @Override
    public String toString() {
        flushBuffer();
        return content.toString();
    }

    public byte[] getContentAsByteArray() {
        flushBuffer();
        return content.toByteArray();
    }

    @Override
    public void flushBuffer() {
        if (writer != null) {
            writer.flush();
        }
    }
}
