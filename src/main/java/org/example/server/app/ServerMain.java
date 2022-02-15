package org.example.server.app;

import org.example.server.app.handler.MainHandler;
import org.example.server.framework.Server;

import java.io.IOException;

// это пишет бизнес-разработчик
public class ServerMain {
    public static void main(String[] args) throws IOException {
        final Server server = new Server();
        server.register("/", new MainHandler());
        server.serve(9999);

    }
}
