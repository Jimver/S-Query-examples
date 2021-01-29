package org.example;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class GetterUtil {
    public static <T, F> F callGetter(T obj, String fieldName) {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(fieldName, obj.getClass());
            F result = (F) pd.getReadMethod().invoke(obj);
            System.out.println(result);
            return result;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
