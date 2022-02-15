package org.example.server.framework.handler;

import org.example.server.framework.http.Request;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public interface Handler {
    void handle(Request request, OutputStream response);
}
