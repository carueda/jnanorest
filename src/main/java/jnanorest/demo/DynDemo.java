package jnanorest.demo;

import jnanorest.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Dynamic route handler for a class given its name.
 * Very simplistic and incomplete.
 */
class DynRouteHandler extends RouteHandler {
    private final Class clazz;
    private final Object obj;
    private final Map<String,Method> mm = new HashMap<String, Method>();
    private final List<String> names = Arrays.asList("get", "post", "put", "delete");

    public DynRouteHandler(String className) throws Exception {
        clazz = Class.forName(className);
        obj = clazz.newInstance();
        for (Method method: clazz.getDeclaredMethods()) {
            String name = method.getName();
            if (names.contains(name)) {
                mm.put(name, method);
            }
        }
    }

    @Override
    public void get(Req req, Res res) throws IOException {
        if (mm.containsKey("get")) {
            dispatch(mm.get("get"), req, res);
        }
        else {
            super.get(req, res);
        }
    }

    @Override
    public void post(Req req, Res res) throws IOException {
        if (mm.containsKey("post")) {
            dispatch(mm.get("post"), req, res);
        }
        else {
            super.post(req, res);
        }
    }

    @Override
    public void put(Req req, Res res) throws IOException {
        if (mm.containsKey("put")) {
            dispatch(mm.get("put"), req, res);
        }
        else {
            super.put(req, res);
        }
    }

    @Override
    public void delete(Req req, Res res) throws IOException {
        if (mm.containsKey("delete")) {
            dispatch(mm.get("delete"), req, res);
        }
        else {
            super.delete(req, res);
        }
    }

    private void dispatch(Method method, Req req, Res res) throws IOException {
        res.headers.put("Content-type", "application/json");
        Throwable t = null;
        try {
            Object ret = method.invoke(obj, req.uri.getRawPath());
            if (ret == null) {
                res.body = "{}";
            }
            res.body = new Json().jsoninfy(ret);
            return;
        }
        catch (InvocationTargetException e) {
            t = e.getTargetException();

        }
        catch (Throwable e) {
            t = e;
        }

        if (t != null) {
            res.status = 503;
            res.body = String.format(
                    "{ \"error\": {\"class\": \"%s\", \"message\": \"%s\", \"stacktrace\": %s } }",
                    t.getClass().getName(), t.getMessage(),
                    new Json().jsoninfy(t.getStackTrace())
            );
        }
    }

    // simple JSONifier
    private static class Json {
        String jsoninfy(Object obj) {
            String json = _jsoninfyObj(obj);
            processed.clear();
            return json;
        }

        private String _jsoninfyObj(Object obj) {
            if (obj == null) {
                return null;
            }
            if (isProcessed(obj)) {
                return '"' + "[!]" + '"';
            }
            processed.add(obj);
            if (obj instanceof Map) {
                return _jsoninfyMap((Map) obj);
            }
            if (obj instanceof Iterable) {
                return _jsoninfyIterable((Iterable) obj);
            }
            if (obj.getClass().isArray()) {
                return _jsoninfyArray(obj);
            }
            return '"' + obj.toString() + '"';
        }

        private String _jsoninfyMap(Map<String, Object> map) {
            StringBuilder sb = new StringBuilder("{");
            String comma = "";
            for (Map.Entry<String,Object> e: map.entrySet()) {
                final Object value = e.getValue();
                String valStr = _jsoninfyObj(value);
                if (valStr != null) {
                    sb.append(String.format("%s\"%s\": %s", comma, e.getKey(), valStr));
                    comma = ", ";
                }
            }
            sb.append("}");
            return sb.toString();
        }

        private String _jsoninfyIterable(Iterable iterable) {
            StringBuilder sb = new StringBuilder("[");
            String comma = "";
            for (Object obj: iterable) {
                String valStr = _jsoninfyObj(obj);
                if (valStr != null) {
                    sb.append(comma);
                    sb.append(valStr);
                    comma = ", ";
                }
            }
            sb.append("]");
            return sb.toString();
        }

        private String _jsoninfyArray(Object array) {
            StringBuilder sb = new StringBuilder("[");
            String comma = "";
            for (int i = 0, len = Array.getLength(array); i < len; i++) {
                Object obj = Array.get(array, i);
                String valStr = _jsoninfyObj(obj);
                if (valStr != null) {
                    sb.append(comma);
                    sb.append(valStr);
                    comma = ", ";
                }
            }
            sb.append("]");
            return sb.toString();
        }

        private boolean isProcessed(Object obj) {
            for (int i = processed.size() -1; i >= 0; i--) {
                if (processed.get(i) == obj) {
                    return true;
                }
            }
            return false;
        }

        private final List<Object> processed = new ArrayList<Object>();
    }
}

// simple demo program.
// http http://localhost:2000/list
// http http://localhost:2000/map
// http http://localhost:2000/array
// http http://localhost:2000
public class DynDemo {
    public static void main(String[] args) throws Exception {
        int port = 2000;
        String className = DynDemo.class.getName();

        JNanoRest server = new JNanoRest(port)
                .route("/", new DynRouteHandler(className))
                ;
        server.start();
        System.out.println("server is listening on port " + port);
        System.in.read();
        System.out.println("server is shutting down...");
        server.stop();
    }

    public Object get(String path) throws Exception {
        if (path.equals("/map")) {
            Map<String, Object> map1 = new HashMap<String, Object>();
            Map<String, Object> map2 = new HashMap<String, Object>();

            map2.put("hello", "world");
            map2.put("map1", map1);

            map1.put("path", path);
            map1.put("map2", map2);

            return map1;
        }
        if (path.equals("/array")) {
            return new int[] {1, 2, 3};
        }
        if (path.equals("/list")) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("hello", "world");
            List<Object> list = new ArrayList<Object>();
            list.add(1);
            list.add(map);
            return list;
        }
        else {
            throw new Exception("simulated exception");
        }
    }
}
