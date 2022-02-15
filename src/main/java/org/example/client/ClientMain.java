package org.example.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientMain {
    public static void main(String[] args) {
        // Загрузка файла (javalog)
        // TODO: Server
        //  1. ServerSocket - bind (port)
        //  2. accept - Socket
        //  3. read/write

        // TODO: Client
        //  1. Socket - с указанием куда мы звоним
        try (
            final Socket socket = new Socket("127.0.0.1", 9999);
            final InputStream in = socket.getInputStream();
            final OutputStream out = socket.getOutputStream();
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        ) {

            buffer.write(
                   """
                   POST / HTTP/1.1\r
                   Host: 127.0.0.1\r
                   Content-Type: image/png\r
                   Content-Length: 36919\r
                   \r
                   """.getBytes(StandardCharsets.UTF_8)
           );
            Files.copy(Paths.get("javalog.png"), buffer);
            // TODO: собираем всё что до этого написали в один массив байт
            out.write(buffer.toByteArray());
            // TODO: отправляй сейчас flush()
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
