package com.qulongjun.team.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON实用类
 * 用来实现对象和JSON字符串的互相转换
 *
 * @author aven
 * @date 2015-01-14
 */
public class JSONUtils {

    public static String object2json(Object obj) {
        StringBuilder json = new StringBuilder();
        if (obj == null) {
            json.append("\"\"");
        } else if (obj instanceof String || obj instanceof Integer
                || obj instanceof Float || obj instanceof Boolean
                || obj instanceof Short || obj instanceof Double
                || obj instanceof Long || obj instanceof BigDecimal
                || obj instanceof BigInteger || obj instanceof Byte) {
            json.append("\"").append(string2json(obj.toString())).append("\"");
        } else if (obj instanceof Object[]) {
            json.append(array2json((Object[]) obj));
        } else if (obj instanceof List) {
            json.append(list2json((List<?>) obj));
        } else if (obj instanceof Map) {
            json.append(map2json((Map<?, ?>) obj));
        } else if (obj instanceof Set) {
            json.append(set2json((Set<?>) obj));
        } else {
            json.append(bean2json(obj));
        }
        return json.toString();
    }

    public static String bean2json(Object bean) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        PropertyDescriptor[] props = null;
        try {
            props = Introspector.getBeanInfo(bean.getClass(), Object.class)
                    .getPropertyDescriptors();
        } catch (IntrospectionException e) {
        }
        if (props != null) {
            for (int i = 0; i < props.length; i++) {
                try {
                    String name = object2json(props[i].getName());
                    String value = object2json(props[i].getReadMethod().invoke(
                            bean));
                    json.append(name);
                    json.append(":");
                    json.append(value);
                    json.append(",");
                } catch (Exception e) {
                }
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }

    public static String list2json(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                json.append(object2json(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String array2json(Object[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (array != null && array.length > 0) {
            for (Object obj : array) {
                json.append(object2json(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String map2json(Map<?, ?> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (map != null && map.size() > 0) {
            for (Object key : map.keySet()) {
                json.append(object2json(key));
                json.append(":");
                json.append(object2json(map.get(key)));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }
        return json.toString();
    }

    public static String set2json(Set<?> set) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (set != null && set.size() > 0) {
            for (Object obj : set) {
                json.append(object2json(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    public static String string2json(String s) {
        if (s == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if (ch >= '\u0000' && ch <= '\u001F') {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    public static <T> T json2Object(String jsonString, Class<T> pojoCalss) {
        if (pojoCalss == null || jsonString == null || jsonString.trim().isEmpty()){
            return null;
        }
        try {
            T pojo = (T)pojoCalss.newInstance();
            Field[] fields = pojo.getClass().getDeclaredFields();

            String simpleName = "";
            for (Field f : fields) {
                try{
                    if (f.getName() == null) {
                        continue;
                    }
                    if (f.getModifiers() > 2) {
                        continue;
                    }
                    f.setAccessible(true);
                    simpleName = f.getType().getSimpleName();

                    String propName = "\"" + f.getName() + "\":";
                    Integer idx = jsonString.indexOf(propName);
                    if (idx == -1){
                        continue;
                    }
                    Integer idxStart = idx + propName.length();
                    if (jsonString.length() < idxStart){
                        continue;
                    }

                    String valStart = jsonString.substring(idxStart, idxStart+1);
                    if (valStart.equals("[")){
                        valStart = "]";
                    }else if (valStart.equals("{")){
                        valStart = "}";
                    }else{
                        if (!valStart.equals("\"")){
                            valStart = ",";
                        }
                    }
                    String propValue = null;
                    Integer idxEnd = -1;
                    if (valStart.equals(",")){
                        idxEnd = jsonString.indexOf(valStart, idxStart);
                        if (idxEnd == -1){
                            continue;
                        }
                        propValue = jsonString.substring(idxStart, idxEnd);
                    }else{
                        idxEnd = jsonString.indexOf(valStart, idxStart + 1);
                        if (idxEnd == -1){
                            continue;
                        }
                        propValue = jsonString.substring(idxStart + 1, idxEnd);
                    }

                    if (propValue == null){
                        continue;
                    }
                    if (simpleName.equals("String") || simpleName.equals("Object")) {
                        f.set(pojo, propValue);
                        continue;
                    }
                    if (simpleName.equals("Long") || simpleName.equals("long")) {
                        f.set(pojo, getLong(propValue));
                        continue;
                    }
                    if (simpleName.equals("Integer") || simpleName.equals("int")) {
                        f.set(pojo, getInteger(propValue));
                        continue;
                    }
                    if (simpleName.equals("Boolean")) {
                        f.set(pojo, getBoolean(propValue));
                        continue;
                    }
                    if (simpleName.equals("Double")) {
                        f.set(pojo, getDouble(propValue));
                        continue;
                    }
                    if (simpleName.equals("Float")) {
                        f.set(pojo, getFloat(propValue));
                        continue;
                    }
                    if (simpleName.equals("Byte")) {
                        f.set(pojo, getByte(propValue));
                        continue;
                    }
                    if (simpleName.equals("List")) {
                        f.set(pojo, getList(propValue));
                        continue;
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            return pojo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long getLong(String propValue){
        Long val = 0L;
        try{
            val = Long.parseLong(propValue);
        }catch(Exception el){}
        return val;
    }

    public static Integer getInteger(String propValue){
        Integer val = 0;
        try{
            val = Integer.parseInt(propValue);
        }catch(Exception el){}
        return val;
    }

    public static Boolean getBoolean(String propValue){
        Boolean val = ("1".equals(propValue) || "true".equalsIgnoreCase(propValue));
        return val;
    }

    public static Double getDouble(String propValue){
        Double val = 0D;
        try{
            val = Double.parseDouble(propValue);
        }catch(Exception el){}
        return val;
    }

    public static Float getFloat(String propValue){
        Float val = 0F;
        try{
            val = Float.parseFloat(propValue);
        }catch(Exception el){}
        return val;
    }

    public static Byte getByte(String propValue){
        Byte val = 0;
        try{
            val = Byte.parseByte(propValue);
        }catch(Exception el){}
        return val;
    }

    public static List<String> getList(String propValue){
        List<String> valList = new ArrayList<String>();
        if (propValue != null && !propValue.trim().isEmpty()){
            try{
                String[] aptEvents = propValue.replace("\"", "").toLowerCase().split(",");
                valList = java.util.Arrays.asList(aptEvents);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return valList;
    }
}