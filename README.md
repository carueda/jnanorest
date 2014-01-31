# jnanorest #

A super simple and lightweight REST server in Java.
The only external dependency is the [google-gson](https://code.google.com/p/google-gson/)
library for the formatting of JSON output.

Note, this was just a quick exercise; the tool is very very limited, but you
could have some fun trying to extend it (make route definitions a bit more
flexible, http-enable regular methods,. etc.)

HTTP server based on
[com.sun.net.httpserver](http://docs.oracle.com/javase/6/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html).

## A demo program ##

```java
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
            res.body = "FooRouteHandler received request: GET " + req.requestUri;
        }
    }

    static class BazRouteHandler extends RouteHandler {
        public void post(Req req, Res res) throws IOException {
            res.status = 202;
            res.body = "BazRouteHandler received request: POST " + req.requestUri;
        }
    }
}
```

## Build and run ##

Using [SBT](http://www.scala-sbt.org/):


    $ sbt
    > run

    Multiple main classes detected, select one to run:

     [1] jnanorest.demo.DynDemo
     [2] jnanorest.demo.Demo

    Enter number: 2

    [info] Running jnanorest.demo.Demo
    server is listening on port 2000

Or using the included script:

    $ ./build.sh
    $ java -jar target/jnanorest.jar
    server is listening on port 2000


Then open http://localhost:2000/foo in your browser,
or use any typical command line tool (like
[wget](http://www.gnu.org/software/wget/),
[curl](http://curl.haxx.se/)
or
[httpie](https://github.com/jkbr/httpie)):


```shell
$ curl -i http://localhost:2000/foo
HTTP/1.1 200 OK
Content-length: 42
Content-type: test/plain

FooRouteHandler received request: GET /foo

$ http POST http://localhost:2000/baz
HTTP/1.1 202 Accepted
Content-length: 43
Content-type: test/plain

BazRouteHandler received request: POST /baz
```

Also, take a look at the dynamic dispatch in `DynDemo.java`.
Again, very simplistic, but perhaps expandable to dispatch
regular code without that code having to extend or implement
any jnanorest types.
```shell
$ java -cp target/jnanorest.jar jnanorest.demo.DynDemo
# and in another terminal for example:
$ http http://localhost:2000/list
```
