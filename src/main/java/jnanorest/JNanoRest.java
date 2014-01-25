package jnanorest;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


/**
 * Super simple REST server based on com.sun.net.httpserver.
 */
public class JNanoRest {
    private final HttpServer server;

    /**
     * Creates a server.
     * @param port  Port to bind to.
     * @throws IOException
     */
    public JNanoRest(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(null);
    }

    /**
     * Adds a route and corresponding handler.
     * @param path          context for the route.
     * @param routeHandler  Handler.
     * @return this
     */
    public JNanoRest route(String path, RouteHandler routeHandler) {
        server.createContext(path, new BaseHandler(routeHandler));
        return this;
    }

    /**
     * Starts the server.
     */
    public void start() {
        server.start();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        server.stop(1);
    }
}

