package jnanorest.demo;

import jnanorest.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Dynamic route handler for a class given its name.
 * Very simplistic.
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
    public void get(Req req, Res res) throws Exception {
        if (mm.containsKey("get")) {
            dispatch(mm.get("get"), req, res);
        }
        else {
            super.get(req, res);
        }
    }

    @Override
    public void post(Req req, Res res) throws Exception {
        if (mm.containsKey("post")) {
            dispatch(mm.get("post"), req, res);
        }
        else {
            super.post(req, res);
        }
    }

    @Override
    public void put(Req req, Res res) throws Exception {
        if (mm.containsKey("put")) {
            dispatch(mm.get("put"), req, res);
        }
        else {
            super.put(req, res);
        }
    }

    @Override
    public void delete(Req req, Res res) throws Exception {
        if (mm.containsKey("delete")) {
            dispatch(mm.get("delete"), req, res);
        }
        else {
            super.delete(req, res);
        }
    }

    private void dispatch(Method method, Req req, Res res) throws Exception {
        res.headers.put("Content-type", "application/json");
        try {
            Object ret = method.invoke(obj, req.uri.getRawPath());
            if (ret == null) {
                res.body = "";
            }
            else {
                res.body = ret;
            }
        }
        catch (InvocationTargetException e) {
            Throwable te = e.getTargetException();
            if (te instanceof Exception) {
                throw (Exception) te;
            }
            else {
                throw new Exception(te);
            }
        }
        catch (IllegalAccessException e) {
            throw new Exception(e.getMessage());
        }
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
            //map1.put("map2", map2); -- gson doesn't detect cycles

            return map1;
        }
        if (path.equals("/array")) {
            return new int[] {1, 2, 3};
        }
        if (path.equals("/null")) {
            return null;
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
