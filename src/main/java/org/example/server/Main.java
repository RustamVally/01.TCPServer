package org.example.server;

import com.google.common.primitives.Bytes;
import org.example.server.exception.InvalidRequestException;
import org.example.server.exception.InvalidRequestLineException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        // порты 0-1024 привилегированные
        try (
                final ServerSocket serverSocket = new ServerSocket(9999);
        ) {
            while (true) {
                try (
                        final Socket socket = serverSocket.accept();
                        final InputStream in = socket.getInputStream();
                        final OutputStream out = socket.getOutputStream();
                ) {
                    final byte[] buffer = new byte[4096];
                    final int read = in.read(buffer);
                    // apache commons (разбито на несколько библиотек)
                    // google guava (не разбито на несколько библиотек)
                    // ищем \r\n
                    final byte[] CRLF = {'\r', '\n'};
                    // ищем в прочитанном массиве байт первый \r\т
                    // по спецификации там будет строка запроса
                    final int requestLineEndIndex = Bytes.indexOf(buffer, CRLF);
                    // -1 если не найдено
                    if (requestLineEndIndex == -1) {
                        throw new InvalidRequestException("no CRLF in request");
                    }

                    final String requestLine = new String(buffer, 0, requestLineEndIndex, StandardCharsets.UTF_8);
                    final String[] requestLineParts = requestLine.split(" ", 3);
                    if (requestLineParts.length != 3) {
                        throw new InvalidRequestLineException(requestLine);
                    }

                    final String requestPath = requestLineParts[1];
                    if (requestPath.equals("/")) {

                        // TODO: 127.0.0.1:9999
                        //  Request:
                        //
                        out.write(
                                """
                                        HTTP/1.1 200 OK\r               
                                        Content-Length: 14\r
                                        Content-Type: text/html\r  
                                        Connection: close\r               
                                        \r
                                        <h1>Hello</h1>
                                        """.getBytes(StandardCharsets.UTF_8)
                        );
                    }
                    if (requestPath.equals("/favicon.ico")) {
                        final Path filePath = Paths.get("favicon.ico");
                        final long size = Files.size(filePath);
                        final String contentType = Files.probeContentType(filePath);
                        out.write((
                                        "HTTP/1.1 200 OK\r\n" +
                                                "Content-Length: " + size + "\r\n" +
                                                "Content-Type: " + contentType + "\r\n" +
                                                "Connection: close\r\n" +
                                                "\r\n"
                                ).getBytes(StandardCharsets.UTF_8)
                        );
                        // записываем сам файл в виде байт
                        Files.copy(filePath, out);
                    }
                }
            }
        }
    }
}
