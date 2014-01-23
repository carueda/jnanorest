package jnanorest.demo;

import jnanorest.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// simple demo program
public class Demo {

    public static void main(String[] args) throws Exception {
        int port = 2000;

        JNanoRest server = new JNanoRest(port)
            .route("/foo", new FooRouteHandler())
            .route("/baz", new BazRouteHandler())
        ;
        server.start();
        System.out.println("server is listening on port " + port);
        System.in.read();
        System.out.println("server is shutting down...");
        server.stop();
    }
    
    static class FooRouteHandler extends RouteHandler {
        public void get(Req req, Res res) throws IOException {
            res.status = 200;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("uri", req.uri);
            map.put("message", "handler \"FooRouteHandler\"");
            res.body = map;
        }
    }

    static class BazRouteHandler extends RouteHandler {
        public void post(Req req, Res res) throws IOException {
            res.status = 202;
            res.body = "BazRouteHandler received request: POST " + req.uri;
        }
    }
}
