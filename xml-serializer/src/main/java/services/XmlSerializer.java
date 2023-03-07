package services;


import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class XmlSerializer {

    @SneakyThrows
    public String serialize(Object object) {
        if (noSerializationNecessary(object))
            return convertXMLCharacters(object.toString());

        StringBuilder output = new StringBuilder();

        if (object instanceof Collection<?> arrayObject) {
            arrayObject.forEach(i ->
                    output
                            .append(wrapInXMLStuff("value", serialize(i))));
        } else if (object instanceof Object[] arrayObject) {
            Arrays.stream(arrayObject).forEach(i ->
                    output
                            .append(wrapInXMLStuff("value", serialize(i))));
        } else if (object instanceof Map<?, ?> map) {
            map.forEach((key, value) ->
                    output
                            .append(wrapInXMLStuff("key", serialize(key)))
                            .append(wrapInXMLStuff("value", serialize(value))));
        } else {
            var fields = object.getClass().getDeclaredFields();
            output.append("<").append(object.getClass().getSimpleName()).append(">");
            for (Field field : fields) {
                try {
                    output.append(wrapInXMLStuff(field.getName(), serialize(getValueOfField(field, object))));
                } catch (IllegalArgumentException e) {
                    //System.out.println("Field not available");
                }
            }
            output.append("</").append(object.getClass().getSimpleName()).append(">");
        }
        return output.toString();
    }

    private String wrapInXMLStuff(String fieldName, String value) {
        return "<" + fieldName + ">" + value + "</" + fieldName + ">";
    }

    @SneakyThrows
    private Object getValueOfField(Field field, Object object) {
        var methods = object.getClass().getMethods();
        String name = field.getName();

        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase("get" + name) || method.getName().equalsIgnoreCase("is" + name)) {
                var output = method.invoke(object);
                if (output == null) throw new IllegalArgumentException();
                return output;
            }
        }
        throw new IllegalArgumentException();
    }

    private String convertXMLCharacters(String string) {
        string = string.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("'", "&apos;")
                .replaceAll("\"", "&quot;");
        return string;
    }

    public static boolean isWrapper(Class<?> clazz) {
        return clazz.equals(Boolean.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Void.class);
    }

    private static boolean noSerializationNecessary(Object object) {
        return object.getClass().isPrimitive() || isWrapper(object.getClass()) || object instanceof String;
    }
}


