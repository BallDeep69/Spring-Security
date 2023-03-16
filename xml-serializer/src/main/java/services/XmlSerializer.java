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

        if (object instanceof Collection<?> collectionObject) {
            collectionObject.forEach(i ->
                    serializedString
                            .append(wrapInXMLStuff("value", serialize(i))));
        } else if (object instanceof Object[] arrayObject) {
            Arrays.stream(arrayObject).forEach(i ->
                    serializedString
                            .append(wrapInXMLStuff("value", serialize(i))));
        } else if (object instanceof Map<?, ?> mapObject) {
            mapObject.forEach((key, value) ->
                    serializedString
                            .append(wrapInXMLStuff("key", serialize(key)))
                            .append(wrapInXMLStuff("value", serialize(value))));
        } else {

            var fields = object.getClass().getDeclaredFields();
            serializedString.append("<").append(object.getClass().getSimpleName()).append(">");
            for (Field field : fields) {
                getValueOfField(field, object)
                        .ifPresent(o -> serializedString.append(wrapInXMLStuff(field.getName(), serialize(o))));
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

    public static boolean isWrapper(Class<?> classToCheck) {
        return classToCheck.equals(Boolean.class) ||
                classToCheck.equals(Character.class) ||
                classToCheck.equals(Byte.class) ||
                classToCheck.equals(Short.class) ||
                classToCheck.equals(Integer.class) ||
                classToCheck.equals(Long.class) ||
                classToCheck.equals(Float.class) ||
                classToCheck.equals(Double.class) ||
                classToCheck.equals(Void.class);
    }
}


