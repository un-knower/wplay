package com.wplay.core.util;

public class NumFormat {

	public static final String ZERO = "0";
	public static final String NINE = "9";
	public static final int DEF_SIZE = 20;
	public static final String DEFAULT_MAX = "99999999999999999999";
	public static final String FILL_WELL = "#";
	public static final String FILL_VERTICAL_LINE = "|";
	public static final int DEF_HOST_SIZE = 200;
	
	/**
	 * ��׺ �Զ���ȫ
	 * @param extKey
	 * @param size ��ȫ��ĸ���
	 * @param fill ������
	 * @return
	 */
	public static final String suffixFill(String extKey,int size,String fill){
		extKey = extKey == null ? "" : extKey;
		StringBuffer sb = new StringBuffer();
		sb.append(extKey);
		int len = extKey.length();
		for(int i = len;i < size; i++){
			sb.append(fill);
		}
		return sb.toString();
	}

	/**
	 * ��׺�Զ���ȫ �� 0 С #
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String suffixByWellFill(String extKey,int size){
		return suffixFill(extKey,size,FILL_WELL);
	}

	/**
	 * ��׺�Զ���ȫ �� z �� |
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String suffixByVerticalLineFill(String extKey,int size){
		return suffixFill(extKey,size,FILL_VERTICAL_LINE);
	}

	/**
	 * ��#��ȫ ��׺ �� 0 С
	 * @param extKey
	 * @return
	 */
	public static final String suffixByWellFill(String extKey){
		return suffixFill(extKey,DEF_HOST_SIZE,FILL_WELL);
	}

	/**
	 * ��|��ȫ ��׺  �� z �� |
	 * @param extKey
	 * @return
	 */
	public static final String suffixByVerticalLineFill(String extKey){
		return suffixFill(extKey,DEF_HOST_SIZE,FILL_VERTICAL_LINE);
	}

	/**
	 * ��ԭ��� #
	 * @param dest
	 * @return
	 */
	public static final String reductionByWellFill(String dest){
		return dest.replaceAll("(#+)$", "");
	}

	/**
	 * ��ԭ��� |
	 * @param dest
	 * @return
	 */
	public static final String reductionByVerticalLineFill(String dest){
		return dest.replaceAll("(\\|+)$", "");
	}

	/**
	 * ǰ׺ #����
	 * @param extKey
	 * @param size
	 * @param fill
	 * @return
	 */
	public static final String prefixFill(String extKey,int size,String fill){
		extKey = extKey == null ? "" : extKey;
		int len = extKey.length();
		StringBuffer sb = new StringBuffer();
		for(int i = len;i < size; i++){
			sb.append(fill);
		}
		sb.append(extKey);
		return sb.toString();
	}

	/**
	 * ǰ׺ #����
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String prefixByWellFill(String extKey,int size){
		return prefixFill(extKey,size,FILL_WELL);
	}

	/**
	 * ǰ׺ |����
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String prefixByVerticalLineFill(String extKey,int size){
		return prefixFill(extKey,size,FILL_VERTICAL_LINE);
	}

	/**
	 * �Զ���ȫ ǰ׺
	 * @param extKey
	 * @param size ��ȫ��ĸ���
	 * @param fill ������
	 * @return
	 */
	public static final String prefixFill(long extKey,int size,String fill){
		String l = Long.toString(extKey);
		int len = l.length();
		StringBuffer sb = new StringBuffer();
		for(int i = len;i < size; i++){
			sb.append(fill);
		}
		sb.append(l);
		return sb.toString();
	}

	/**
	 * ��#��ȫ ǰ׺
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String prefixByWellFill(long extKey,int size){
		return prefixFill(extKey,size,FILL_WELL);
	}

	/**
	 * ��0��ȫ
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String suffixByZero(long extKey,int size){
		return suffixFill(extKey,size,ZERO);
	}

	/**
	 * �Զ���ȫ ��׺
	 * @param extKey
	 * @param size ��ȫ��ĸ���
	 * @param fill ������
	 * @return
	 */
	public static final String suffixFill(long extKey,int size,String fill){
		String l = Long.toString(extKey);
		int len = l.length();
		StringBuffer sb = new StringBuffer();
		for(int i = len;i < size; i++){
			sb.append(fill);
		}
		sb.append(l);
		return sb.toString();
	}


	/**
	 * ��|��ȫ ǰ׺
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String prefixByVerticalLineFill(long extKey,int size){
		return prefixFill(extKey,size,FILL_VERTICAL_LINE);
	}

	/**
	 * ��0��ȫ
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String prefixByZero(long extKey,int size){
		return prefixFill(extKey,size,ZERO);
	}

	/**
	 * ��0��ȫ
	 * @param extKey
	 * @return
	 */
	public static final String formatByZero(long extKey){
		return prefixFill(extKey,DEF_SIZE,ZERO);
	}

	/**
	 * ��9��ȫ
	 * @param extKey
	 * @param size
	 * @return
	 */
	public static final String formatByNine(long extKey,int size){
		return prefixFill(extKey,size,NINE);
	}

	/**
	 * ��9��ȫ
	 * @param extKey
	 * @return
	 */
	public static final String formatByNine(long extKey){
		return prefixFill(extKey,DEF_SIZE,NINE);
	}

	/**
	 * Ĭ�����ֵ
	 * @return
	 */
	public static final String getDefault(){
		return DEFAULT_MAX;
	}
}
