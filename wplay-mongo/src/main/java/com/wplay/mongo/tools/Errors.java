package com.wplay.mongo.tools;

/** 
 * @author         lolog
 * @version        V1.0  
 * @date           2016.08.18
 * @company        WEIGO
 * @description    error code
*/

public class Errors {
	public static Boolean SUCCESS                  = true; 
	public static Boolean ERROR                    = false;
	
	public static Integer ERROR_CODE_NONE          = 0;
	public static Integer ERROR_CODE_LOGIN_TIMEOUT = 1;
	public static Integer ERROR_CODE_ASK_TIMEOUT   = 2;
	public static Integer ERROR_CODE_ADD           = 3;
	public static Integer ERROR_CODE_DELETE        = 4;
	public static Integer ERROR_CODE_UPDATE        = 5;
	public static Integer ERROR_CODE_SELECT        = 6;
	public static Integer ERROR_CODE_PARAMETER     = 7;
	public static Integer ERROR_CODE_CSRF          = 8;
	
	public static Integer ERROR_CODE_DATA_EXISTS       = 9;
	public static Integer ERROR_CODE_DATA_NOT_EXISTS   = 10;
	
	public static Integer ERROR_CODE_FILE              = 11;
	
	public static String ERROR_MESSAGE_LOGIN_ZH        = "��¼��ʱ";
	public static String ERROR_MESSAGE_ASK_ZH          = "����ʱ";
	public static String ERROR_MESSAGE_ADD_ZH          = "����ʧ��";
	public static String ERROR_MESSAGE_DELETE_ZH       = "ɾ��ʧ��";
	public static String ERROR_MESSAGE_UPDATE_ZH       = "����ʧ��";
	public static String ERROR_MESSAGE_SELECT_ZH       = "��ѯʧ��";
	public static String ERROR_MESSAGE_PARAMETERS_ZH   = "��������Ƿ�";
	public static String ERROR_MESSAGE_CSRF_ZH         = "�Ƿ��ύ";
	public static String ERROR_MESSAGE_REPEAT_ZH       = "�ظ��ύ";

	public static String ERROR_MESSAGE_FILE_ZH         = "�ļ�Ϊ��";

	public static String ERROR_MESSAGE_DATA_EXISTS_ZH      = "�Ѵ���";
	public static String ERROR_MESSAGE_DATA_NOT_EXISTS_ZH  = "������";

	public static String SUCCESS_MESSAGE_LOAD_ZH         = "���سɹ�";
	public static String SUCCESS_MESSAGE_ADD_ZH          = "�����ɹ�";
	public static String SUCCESS_MESSAGE_DELETE_ZH       = "ɾ���ɹ�";
	public static String SUCCESS_MESSAGE_UPDATE_ZH       = "���³ɹ�";
	public static String SUCCESS_MESSAGE_SELECT_ZH       = "��ѯ�ɹ�";
} 
