package jnanorest;

/**
 * Base class with default behavior for route handlers.
 */
public class RouteHandler {

    public void get(Req req, Res res) throws Exception {
        res.status = 404;
        res.body = "Resource not found: " + req.uri;
    }

    public void post(Req req, Res res) throws Exception {
        invalidMethod(req, res, "POST");
    }

    public void put(Req req, Res res) throws Exception {
        invalidMethod(req, res, "PUT");
    }

    public void delete(Req req, Res res) throws Exception {
        invalidMethod(req, res, "DELETE");
    }

    static void invalidMethod(Req req, Res res, String method) {
        res.status = 405;
        res.body = "invalid method " + method;
    }
}
