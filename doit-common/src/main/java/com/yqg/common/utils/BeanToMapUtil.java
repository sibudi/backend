package com.yqg.common.utils;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * bean ? map ?? ???
 * Created by Jacob on 16/9/12.
 */
public class BeanToMapUtil {

    /**
     * ??? Map ??????? JavaBean
     * @param clazz ??????
     * @param map ?????? map
     * @return ????? JavaBean ??
     * @throws IntrospectionException
     *             ?????????
     * @throws IllegalAccessException
     *             ????? JavaBean ??
     * @throws InstantiationException
     *             ????? JavaBean ??
     * @throws InvocationTargetException
     *             ??????? setter ????
     */
    public static <T> T convertMap(Class<T> clazz, Map map)
            throws IntrospectionException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz); // ?????
        T obj = clazz.newInstance(); // ?? JavaBean ??

        // ? JavaBean ???????
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();

            if (map.containsKey(propertyName)) {
                // ?????? try ?????????????????????????????
                Object value = map.get(propertyName);

                Object[] args = new Object[1];
                args[0] = value;

                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }

    /**
     * ??? JavaBean ???????  Map
     * @param bean ????JavaBean ??
     * @return ?????  Map ??
     * @throws IntrospectionException ?????????
     * @throws IllegalAccessException ????? JavaBean ??
     * @throws InvocationTargetException ??????? setter ????
     */
    public static Map convertBean(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }
}
