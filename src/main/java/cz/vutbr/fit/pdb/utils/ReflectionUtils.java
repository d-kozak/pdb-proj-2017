package cz.vutbr.fit.pdb.utils;


import java.lang.reflect.Field;

public class ReflectionUtils {

    public static void setField(Object object, String name, Object value) {
        try {
            Class<?> aClass = object.getClass();
            Field field = aClass.getField(name);
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
