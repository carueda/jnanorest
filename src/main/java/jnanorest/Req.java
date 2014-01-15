package jnanorest;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Request object.
 */
public class Req {
    final public URI uri;
    final public Map<String, List<String>> headers;
    final public String body;

    public Req(URI uri,
               Map<String, List<String>> headers,
               String body) {

        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }
}

