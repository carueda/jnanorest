package jnanorest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple JSONifier.
 *
 * @author carueda.
 */
public class Json {

    /**
     * Gets a JSON representation of the given object.
     * @param obj The object.
     *
     * @return the result. It will be null if the argument is null.
     */
    public String stringify(Object obj) {
        String json = _stringifyObj(obj);
        processed.clear();
        return json;
    }

    private String _stringifyObj(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map || obj instanceof Iterable || obj.getClass().isArray()) {
            // the composite cases that are handled.
            if (isProcessed(obj)) {
                return '"' + "[!]" + '"';  // recursive structure
            }
            processed.add(obj);
            if (obj instanceof Map) {
                return _stringifyMap((Map) obj);
            }
            else if (obj instanceof Iterable) {
                return _stringifyIterable((Iterable) obj);
            }
            else {
                assert obj.getClass().isArray();
                return _stringifyArray(obj);
            }
        }
        else {
            String escaped = obj.toString().replaceAll("\"", "\\\\\"");
            return '"' + escaped + '"';
        }
    }

    private String _stringifyMap(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        String comma = "";
        for (Map.Entry<String,Object> e: map.entrySet()) {
            final Object value = e.getValue();
            String valStr = _stringifyObj(value);
            if (valStr != null) {
                sb.append(String.format("%s\"%s\": %s", comma, e.getKey(), valStr));
                comma = ", ";
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String _stringifyIterable(Iterable iterable) {
        StringBuilder sb = new StringBuilder("[");
        String comma = "";
        for (Object obj: iterable) {
            String valStr = _stringifyObj(obj);
            if (valStr != null) {
                sb.append(comma);
                sb.append(valStr);
                comma = ", ";
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String _stringifyArray(Object array) {
        StringBuilder sb = new StringBuilder("[");
        String comma = "";
        for (int i = 0, len = Array.getLength(array); i < len; i++) {
            Object obj = Array.get(array, i);
            String valStr = _stringifyObj(obj);
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

    // check for only the composite objects handled
    private final List<Object> processed = new ArrayList<Object>();
}
