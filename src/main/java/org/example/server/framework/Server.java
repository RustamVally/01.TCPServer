package org.example.server.framework;


import org.example.server.framework.guava.Bytes;
import org.example.server.framework.exception.InvalidRequestException;
import org.example.server.framework.exception.InvalidRequestLineException;
import org.example.server.framework.exception.RequestHeadersTooLargeException;
import org.example.server.framework.handler.Handler;
import org.example.server.framework.http.Request;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {
    public static final byte[] CRLF = {'\r', '\n'};
    public static final byte[] CRLFCRLF = {'\r', '\n', '\r', '\n'};

    // register ("/", мой метод)
    private final Map<String, Handler> handlers = new HashMap<>();

    public void register(final String path, final Handler handler) {
        handlers.put(path, handler);
    }

    public void serve (final int port) throws IOException {
        // порты 0-1024 привилегированные
        try (
                final ServerSocket serverSocket = new ServerSocket(port);
        ) {
            while (true) {
                try (
                        final Socket socket = serverSocket.accept();
                        final InputStream in = new BufferedInputStream(socket.getInputStream());
                        final OutputStream out = socket.getOutputStream();
                ) {
                    final byte[] buffer = new byte[4096];
                    in.mark(buffer.length); // установка метки mark на начало чтения (не более 4096 байт)
                    final int read = in.read(buffer);
                    // apache commons (разбито на несколько библиотек)
                    // google guava (не разбито на несколько библиотек)
                    // ищем \r\n
                    // ищем в прочитанном массиве байт первый \r\т
                    // по спецификации там будет строка запроса
                    final int requestLineEndIndex = Bytes.indexOf(buffer, CRLF, 0);
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

                    final int headersStartIndex = requestLineEndIndex + CRLF.length;

                    // индекс, который отделяет заголовки от тела запроса (если оно есть)
                    final int headersEndIndex = Bytes.indexOf(buffer, CRLFCRLF, 0);
                    if (headersEndIndex == -1) {
                        throw new RequestHeadersTooLargeException();
                    }

                    final Map<String, String> headers = new HashMap<>();

                    int contentLength = 0;

                    // текущий индекс который перемещаем по массиву
                    int currentIndex = headersStartIndex;
                    while (currentIndex < headersEndIndex) {
                        // currentIndex - 17
                        // currentHeaderEndIndex - 32
                        final int currentHeaderEndIndex = Bytes.indexOf(buffer, CRLF, currentIndex);
                        final String header = new String(buffer, currentIndex, (currentHeaderEndIndex - currentIndex), StandardCharsets.UTF_8);

                        currentIndex = currentHeaderEndIndex + CRLF.length;

                        // Content-Length: 36919
                        final String[] headerParts = header.split(":", 2);
                        // Content-Length
                        final String headerName = headerParts[0].trim();
                        // 36919
                        final String headerValue = headerParts[1].trim();
                        headers.put(headerName, headerValue);

                        if (headerName.equals("Content-Length")) {
                            contentLength = Integer.parseInt(headerValue);
                        }
                    }

                    // 1-10 Mb
                    if (contentLength > 1024 * 1024) {
                        throw new RuntimeException("..."); // TODO: add custom exception
                    }

                    in.reset(); // прыгает на метку
                    final int bodyStarIndex = headersEndIndex + CRLFCRLF.length;
                    final int bodyEndIndex = bodyStarIndex + currentIndex;
                    final long skipped = in.skip(bodyStarIndex);// перепрыгиваем на начало
                    if (skipped != bodyStarIndex) {
                        throw new RuntimeException("..."); // TODO: add custom exception
                    }

                    final byte[] body = in.readNBytes(contentLength);

                    final Request request = new Request();
                    request.setPath(requestPath);
                    request.setContentLength(contentLength);
                    request.setHeaders(headers);
                    request.setBody(body);

                    // TODO: NPE

                handlers.get(requestPath).handle(request, out);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
