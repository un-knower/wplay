package com.wplay.mongo.annotations;

import java.lang.annotation.*;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016��7��8��
 * @Company        WEGO
 * @Description    ��������
*/
@Inherited   // ����ᱻ�̳�
@Documented  // �ĵ���
@Retention(RetentionPolicy.RUNTIME) // ����ʱ���Ի�ȡ
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Index {
	// �Ƿ�������
	public boolean key() default true;
	// Ψһ����
	public boolean unique() default false;
    // ������ʽ
    public int order() default 1;
    // ��������
    public String name() default "";
    // ObjectId����
    public boolean id() default false;
    // omission
    public boolean omission() default true;
    // ��̨��ʽ��������
    public boolean background() default true;
    // �ĵ��в����ڵ��ֶ����ݲ���������������Ϊtrue,�����ֶ��в����ѯ����������Ӧ�ֶε��ĵ�
    public boolean sparse() default false;
}
