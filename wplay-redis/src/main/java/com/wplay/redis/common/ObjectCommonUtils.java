package com.wplay.redis.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>
 * ����������תmap
 * </p>
 *
 * @author James
 * @version 1.0
 * @Date 18/01/10
 */
public class ObjectCommonUtils {

    public static void main(String[] a) {

    }

    /**
     * �����ڲಶ���쳣��ֻ�ᶪʧ����Ĳ����Ĵ��룬�����ڲ����쳣�Ĳ��ִ�ӡ������־�����㿪����Ա���ԣ���������ѭ������
     * �쳣�ķ�ʽ�Գ����ִ��Ч��Ӱ��ϴ�
     *
     * @param o
     * @param fields
     * @return
     * @throws Exception
     * @version 1.0
     */
    public static Map<String, String> objResolveToMap(Object o, String... fields) {

        List<String> fieldList = new ArrayList<>();
        if (fields == null || fields.length == 0) {
            // �������������������
            fieldList = getAllFieldName(o.getClass());
        } else {
            fieldList.addAll(Arrays.asList(fields));
        }
        Map<String, String> map = new HashMap<>();

        // �ж�������Ƿ�������,���ߴ��ݵ�����ֵ�Ƿ���ȷ
        if (fieldList.isEmpty()) {
            return map;
        }

        // �������Բ���ȡֵ
        for (String field : fieldList) {
            // ��ȡ��������Ӧ������ֵ
            Object value = null;
            try {
                value = getMethodInvoke(o, field);
            } catch (Exception e) {
            }
            if (value == null) {
                continue;
            }

            map.put(field, String.valueOf(value));
        }

        return map;
    }

    /**
     * ִ�������������Ե�get����ȡֵ
     *
     * @param object
     * @param field
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Object getMethodInvoke(T object, String field) throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor(field, object.getClass());
        Method wM = pd.getReadMethod();//���д����
        return wM.invoke(object);
    }

    /**
     * ��ö��������������
     *
     * @param clazz
     * @return
     */
    public static List<String> getAllFieldName(Class<?> clazz) {
        List<String> allFieldName = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if ("serialVersionUID".equals(fieldName)) {
                continue;
            }

            // TODO: fieldName�ǿ�У��
            allFieldName.add(fieldName);
        }
        return allFieldName;
    }

    /**
     * ��㲹���쳣,����ӡ��־,�ڴ����������֮�����������ֵ���޷���ȡ,��ʹ���쳣Ҳ������ȷִ��,����û��v1��Ч��Ӱ���
     *
     * @param o
     * @param fields
     * @return
     * @throws Exception
     * @version 2.0
     */
    public static Map<String, String> objResolveToMapV2(Object o, String... fields) {

        List<String> fieldList = new ArrayList<>();
        if (fields == null || fields.length == 0) {
            // �������������������
            fieldList = getAllFieldName(o.getClass());
        } else {
            fieldList.addAll(Arrays.asList(fields));
        }
        Map<String, String> map = new HashMap<>();

        // �ж�������Ƿ�������,���ߴ��ݵ�����ֵ�Ƿ���ȷ
        if (fieldList.isEmpty()) {
            return map;
        }

        // �������Բ���ȡֵ
        try {
            for (String field : fieldList) {
                // ��ȡ��������Ӧ������ֵ
                Object value = null;

                value = getMethodInvoke(o, field);

                if (value == null) {
                    continue;
                }

                map.put(field, String.valueOf(value));
            }
        } catch (Exception e) {
        }
        return map;
    }

    /**
     * �׳��쳣�ÿ�����Ա��飬������쳣��δ��뽫������ȷִ��
     *
     * @param o
     * @param fields
     * @return
     * @throws Exception
     * @version 3.0
     */
    public static Map<String, String> objResolveToMapV3(Object o, String... fields)
            throws Exception {

        List<String> fieldList = new ArrayList<>();
        if (fields == null || fields.length == 0) {
            // �������������������
            fieldList = getAllFieldName(o.getClass());
        } else {
            fieldList.addAll(Arrays.asList(fields));
        }
        Map<String, String> map = new HashMap<>();

        // �ж�������Ƿ�������,���ߴ��ݵ�����ֵ�Ƿ���ȷ
        if (fieldList.isEmpty()) {
            return map;
        }

        // �������Բ���ȡֵ
        for (String field : fieldList) {
            // ��ȡ��������Ӧ������ֵ
            Object value = null;

            value = getMethodInvoke(o, field);

            if (value == null) {
                continue;
            }

            map.put(field, String.valueOf(value));
        }
        return map;
    }

    /**
     * ��һ�� JavaBean ����ת��Ϊһ��  Map
     *
     * @param bean Ҫת����JavaBean ����
     * @return ת��������  Map ����
     * @throws IntrospectionException    �������������ʧ��
     * @throws IllegalAccessException    ���ʵ���� JavaBean ʧ��
     * @throws InvocationTargetException ����������Ե� setter ����ʧ��
     */
    public static Map<String, String> convertBean(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map<String, String> returnMap = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, String.valueOf(result));
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }

}
