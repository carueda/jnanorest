package jnanorest;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * Http handler for a route handler.
 */
class BaseHandler implements HttpHandler {

    final RouteHandler routeHandler;

    BaseHandler(RouteHandler routeHandler) {
        this.routeHandler = routeHandler;
    }

    public void handle(HttpExchange exchange) throws IOException {

        Req req = createReq(exchange);
        Res res = createRes(exchange);

        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            routeHandler.get(req, res);
        }
        else if (requestMethod.equalsIgnoreCase("POST")) {
            routeHandler.post(req, res);
        }
        else if (requestMethod.equalsIgnoreCase("PUT")) {
            routeHandler.put(req, res);
        }
        else if (requestMethod.equalsIgnoreCase("DELETE")) {
            routeHandler.delete(req, res);
        }
        else {
            RouteHandler.invalidMethod(req, res, requestMethod);
        }

        Headers resHeaders = exchange.getResponseHeaders();
        for (Map.Entry<String,String> e : res.headers.entrySet()) {
            resHeaders.set(e.getKey(), e.getValue());
        }
        if (!resHeaders.containsKey("Content-Type")) {
            resHeaders.set("Content-Type", "text/plain");
        }

        byte[] bytes = res.body.getBytes();
        exchange.sendResponseHeaders(res.status, bytes.length);
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(bytes);
        responseBody.close();

        clf(exchange, req, res);
    }

    private Req createReq(HttpExchange exchange) throws IOException {
        String body = "";
        InputStream is = exchange.getRequestBody();
        try {
            body = convertStreamToString(is);
        }
        finally {
            is.close();
        }
        return new Req(exchange.getRequestURI(),
                exchange.getRequestHeaders(),
                body);
    }

    private Res createRes(HttpExchange exchange) throws IOException {
        return new Res();
    }

    private static SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z");

    // prints a line a la http://httpd.apache.org/docs/1.3/logs.html#common
    private static void clf(HttpExchange exchange, Req req, Res res) {
        String h = exchange.getRemoteAddress().getHostName();
        String l = "-";  // rfc1413
        String u = "-";
        String t = df.format(new Date());
        String r = String.format("%s %s %s",
                exchange.getRequestMethod(), req.uri, exchange.getProtocol());
        String s = String.valueOf(res.status);
        String b = String.valueOf(res.body.getBytes().length);

        System.out.printf("%s %s %s [%s] \"%s\" %s %s%n",
                h, l, u, t, r, s, b
        );
    }

    // http://stackoverflow.com/a/5445161/830737
    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
