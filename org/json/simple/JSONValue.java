/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json.simple;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONValue {
    public static Object parse(Reader in) {
        try {
            JSONParser parser = new JSONParser();
            return parser.parse(in);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Object parse(String s) {
        StringReader in = new StringReader(s);
        return JSONValue.parse(in);
    }

    public static Object parseWithException(Reader in) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(in);
    }

    public static Object parseWithException(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(s);
    }

    public static void writeJSONString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }
        if (value instanceof String) {
            out.write(34);
            out.write(JSONValue.escape((String)value));
            out.write(34);
            return;
        }
        if (value instanceof Double) {
            if (!((Double)value).isInfinite() && !((Double)value).isNaN()) {
                out.write(value.toString());
                return;
            }
            out.write("null");
            return;
        }
        if (value instanceof Float) {
            if (!((Float)value).isInfinite() && !((Float)value).isNaN()) {
                out.write(value.toString());
                return;
            }
            out.write("null");
            return;
        }
        if (value instanceof Number) {
            out.write(value.toString());
            return;
        }
        if (value instanceof Boolean) {
            out.write(value.toString());
            return;
        }
        if (value instanceof JSONStreamAware) {
            ((JSONStreamAware)value).writeJSONString(out);
            return;
        }
        if (value instanceof JSONAware) {
            out.write(((JSONAware)value).toJSONString());
            return;
        }
        if (value instanceof Map) {
            JSONObject.writeJSONString((Map)value, out);
            return;
        }
        if (value instanceof List) {
            JSONArray.writeJSONString((List)value, out);
            return;
        }
        out.write(value.toString());
    }

    public static String toJSONString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return new StringBuffer().append("\"").append(JSONValue.escape((String)value)).append("\"").toString();
        }
        if (value instanceof Double) {
            if (((Double)value).isInfinite()) return "null";
            if (!((Double)value).isNaN()) return value.toString();
            return "null";
        }
        if (value instanceof Float) {
            if (((Float)value).isInfinite()) return "null";
            if (!((Float)value).isNaN()) return value.toString();
            return "null";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof JSONAware) {
            return ((JSONAware)value).toJSONString();
        }
        if (value instanceof Map) {
            return JSONObject.toJSONString((Map)value);
        }
        if (!(value instanceof List)) return value.toString();
        return JSONArray.toJSONString((List)value);
    }

    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        JSONValue.escape(s, sb);
        return sb.toString();
    }

    /*
     * Unable to fully structure code
     */
    static void escape(String s, StringBuffer sb) {
        i = 0;
        while (i < s.length()) {
            ch = s.charAt(i);
            switch (ch) {
                case '\"': {
                    sb.append("\\\"");
                    ** break;
                }
                case '\\': {
                    sb.append("\\\\");
                    ** break;
                }
                case '\b': {
                    sb.append("\\b");
                    ** break;
                }
                case '\f': {
                    sb.append("\\f");
                    ** break;
                }
                case '\n': {
                    sb.append("\\n");
                    ** break;
                }
                case '\r': {
                    sb.append("\\r");
                    ** break;
                }
                case '\t': {
                    sb.append("\\t");
                    ** break;
                }
                case '/': {
                    sb.append("\\/");
                    ** break;
                }
            }
            if (ch >= '\u0000' && ch <= '\u001f' || ch >= '' && ch <= '\u009f' || ch >= '\u2000' && ch <= '\u20ff') {
                ss = Integer.toHexString(ch);
                sb.append("\\u");
                for (k = 0; k < 4 - ss.length(); ++k) {
                    sb.append('0');
                }
                sb.append(ss.toUpperCase());
                ** break;
            }
            sb.append(ch);
lbl50: // 10 sources:
            ++i;
        }
    }
}

