package com.wplay.core.vo;

import org.w3c.dom.Element;

public interface XmlAble {
	
	/**
	 * ��xml ����Ϊ����
	 * @param node
	 * @return
	 */
	public void parse(Element node);

	/**
	 * �������Ϊxml
	 * @return
	 */
	public Element toNode();
	
	
}
