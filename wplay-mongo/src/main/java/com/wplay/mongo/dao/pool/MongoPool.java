package com.wplay.mongo.dao.pool;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * @author         lolog
 * @version        V1.0  
 * @Date           2016��6��20��
 * @Company        CIMCSSC
 * @Description    MongoDB���ݿ����ӳ�
 */
public interface MongoPool {
	/**
	 * @Title               getMongoClient
	 * @author              lolog
	 * @Description         ��ȡMongoClient
	 * @return              �������ӵ�MongoClient
	 * @Date                2016��6��21�� ����10:22:45
	 */
	public MongoClient getMongoClient();

	/**
	 * @Title        getDB
	 * @author       lolog
	 * @Description  ��ȡ���ӵ����ݿ�
	 * @return       �������ӵ����ݿ�
	 * @Date         2016-6-20 18:04:07
	 */
	public MongoDatabase getDB();

	/**
	 * @Title              getDB
	 * @author             lolog
	 * @Description        ��ȡ���ӵ����ݿ�
	 * @param databaseName ���ݿ���
	 * @param userName     �û���
	 * @param password     ����
	 * @return             �������ӵ����ݿ�
	 * @Date               2016-6-20 18:04:07
	 */
	public MongoDatabase getDB(String database, String userName, String password);
}
