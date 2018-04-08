package com.wplay.mongo.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.bson.Document;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *  @author James
 */
public class MongoBean implements Cloneable   {

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }

    //����tojson tojsonString��Ҫget set��������public���� ��ѡһ �����
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    //�������
    public JSONObject toJSONObject() {
        return (JSONObject) JSONObject.toJSON(this);
    }

    //�������
    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    //����get set����  �������
    public Map<String, Object> toMap() {
        if (this == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                /*����class����  */
                if (!key.equals("class")) {
                    //*�õ�property��Ӧ��getter����*/
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(this);

                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    //��������private��������get set���������� �������
    public Map<String, Object> objectObjectMap() {
        if (this == null) {
            return null;
        }
        HashMap map = new HashMap(40);
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public Document toDocument() {
        return Document.parse(toJSONString());
    }

}
