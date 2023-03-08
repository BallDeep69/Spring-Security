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

        StringBuilder serializedString = new StringBuilder();

        if (object instanceof Collection<?> arrayObject) {
            arrayObject.forEach(i ->
                    serializedString
                            .append(wrapInXMLStuff("value", serialize(i))));
        } else if (object instanceof Object[] arrayObject) {
            Arrays.stream(arrayObject).forEach(i ->
                    serializedString
                            .append(wrapInXMLStuff("value", serialize(i))));
        } else if (object instanceof Map<?, ?> map) {
            map.forEach((key, value) ->
                    serializedString
                            .append(wrapInXMLStuff("key", serialize(key)))
                            .append(wrapInXMLStuff("value", serialize(value))));
        } else {

            var fields = object.getClass().getDeclaredFields();
            serializedString.append("<").append(object.getClass().getSimpleName()).append(">");
            for (Field field : fields) {
                var fieldValue = getValueOfField(field, object);
                fieldValue.ifPresent(o -> serializedString.append(wrapInXMLStuff(field.getName(), serialize(o))));
            }
            serializedString.append("</").append(object.getClass().getSimpleName()).append(">");

        }
        return serializedString.toString();
    }

    private String wrapInXMLStuff(String fieldName, String value) {
        return "<" + fieldName + ">" + value + "</" + fieldName + ">";
    }

    /**
     * Sucht zum Field eines objekts die passende Getter-Methode anhand des Namens und returnt den
     * Invoke-Wert als Optional
     *
     * @return Optional mit not-null Wert des Felds oder Optional.empty()
     */

    @SneakyThrows
    private Optional<?> getValueOfField(Field field, Object object) {
        var methods = object.getClass().getMethods();
        String fieldName = field.getName();

        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase("get" + fieldName) ||
                    method.getName().equalsIgnoreCase("is" + fieldName)) {
                return Optional.ofNullable(method.invoke(object));
            }
        }

        return Optional.empty();
    }

    private static boolean noSerializationNecessary(Object object) {
        return object.getClass().isPrimitive() || isWrapper(object.getClass()) || object instanceof String;
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
}


