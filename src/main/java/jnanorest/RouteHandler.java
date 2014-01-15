package jnanorest;

import java.io.IOException;

/**
 * Base class with default behavior for route handlers.
 */
public class RouteHandler {

    public void get(Req req, Res res) throws IOException {
        res.status = 404;
        res.body = "Resource not found: " + req.uri;
    }

    public void post(Req req, Res res) throws IOException {
        invalidMethod(req, res, "POST");
    }

    public void put(Req req, Res res) throws IOException {
        invalidMethod(req, res, "PUT");
    }

    public void delete(Req req, Res res) throws IOException {
        invalidMethod(req, res, "DELETE");
    }

    static void invalidMethod(Req req, Res res, String method) {
        res.status = 405;
        res.body = "invalid method " + method;
    }
}
