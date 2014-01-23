package jnanorest;

import java.util.HashMap;
import java.util.Map;

/**
 * Response object. RouteHandler sets values here for the
 * desired response.
 */
public class Res {
    public int    status = 200;
    public Object body = "";
    public Map<String,String> headers = new HashMap<String, String>();
}
