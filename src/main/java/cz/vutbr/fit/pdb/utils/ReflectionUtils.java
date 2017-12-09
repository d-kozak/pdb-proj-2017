package cz.vutbr.fit.pdb.utils;


import java.lang.reflect.Field;

public class ReflectionUtils {

    public static void setValue(Object object, String name, Object value) {
        try {
            Class<?> aClass = object.getClass();
            Field field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getValue(Object object, String name) {
        Field field = null;
        try {
            Class<?> aClass = object.getClass();
            field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            if (field != null)
                field.setAccessible(false);
        }
    }
}
