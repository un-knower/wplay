package com.wplay.hbase;

import java.io.IOException;
import java.util.List;

/**
 * Hbase ��ɾ�Ĳ���
 * @author James
 *
 */
public interface HbaseCrud {

	/**
	 * ������
	 * @param tableName
	 * @param famliys
	 * @throws IOException
	 */
	void createTalbe(String tableName, String[] famliys) throws IOException;

	/**
	 * ɾ����
	 * @param tableName
	 * @throws IOException
	 */
	void dorpTable(String tableName) throws IOException;

	/**
	 * ����һ��
	 * @param tableName
	 * @param rowKey
	 * @param famliy
	 * @param qualifier
	 * @param value
	 */
	void insertRow(String tableName, String rowKey, String famliy, String qualifier, Object value) throws IOException;

	/**
	 * ɾ��һ��
	 * @param tableName
	 * @param rowKey
	 * @throws IOException
	 */
	void deleteRow(String tableName, String rowKey) throws IOException;

	/**
	 * ɾ������
	 * @param tableName
	 * @param rowKeys
	 * @throws IOException
	 */
	void deleteRow(String tableName, List<String> rowKeys) throws IOException;

	/**
	 *
	 * @param tableName
	 * @param rowkey
	 * @param famliy
	 * @param qualifier
	 * @throws IOException
	 */
	void deleteCell(String tableName, String rowkey, String famliy, String qualifier) throws IOException;

	/**
	 * ����cell
	 * @param tableName
	 * @param famliy
	 * @param qualifier
	 * @param value
	 * @throws IOException
	 */
	void updateCell(String tableName, String rowkey, String famliy, String qualifier, Object value)  throws IOException;
	
}
