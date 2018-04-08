package com.wplay.mongo.util.impl;

import com.wplay.mongo.annotations.EKey;
import com.wplay.mongo.annotations.Index;
import com.wplay.mongo.tools.Tool;
import com.wplay.mongo.util.DocumentUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;


/**
 * @author         lolog
 * @version        V1.0  
 * @Date           2016.07.08
 * @Company        CIMCSSC
 * @Description    MongoDB Change Class
*/

@SuppressWarnings({"unchecked", "rawtypes", "null"})
public class DocumentUtilImpl<T> implements DocumentUtil<T> {
	
	// T��Class����
	private Class clazz;
	// T�̳еĶ���
	private Class superClazz;

	public DocumentUtilImpl() {
		// ��ȡT����
		ParameterizedType parameters = (ParameterizedType) this.getClass().getGenericSuperclass();
		clazz = (Class) parameters.getActualTypeArguments()[0];
		superClazz = clazz.getSuperclass();
	}

	@Override
	public List<IndexModel> getIndexs (T t) {
		// ��ȡ���������
		Field[] clazz_fields      = clazz.getDeclaredFields();
		// ��ȡT�����������
		Field[] superFields       = superClazz.getDeclaredFields();

		Field[] fields = new Field[clazz_fields.length + superFields.length];

		// �ϲ�T��T�����������
		System.arraycopy(clazz_fields, 0, fields, 0, clazz_fields.length);
		// �ϲ�T��T�����������
		System.arraycopy(superFields, 0, fields, clazz_fields.length, superFields.length);

		// ������������
		List<IndexModel> indexModels = new ArrayList<IndexModel>();

		// ѭ�����������
		for (Field field: fields) {
			// ������Ա�Indexע��,��ô
			if (field.isAnnotationPresent(Index.class)) {
				// ��ȡע�����
				Index index = field.getAnnotation(Index.class);

				// MongoDB ObjectID�������߲�������
				if(index.id() == true || index.key() == false) {
					continue;
				}

				// ����������
				BasicDBObject keys = new BasicDBObject();
				keys.append(field.getName(), index.order());

				// ����ѡ��
				IndexOptions indexOptions = new IndexOptions();

				if(index.background()) {
					indexOptions.background(index.background());
				}

				// ��������
				if("".equals(index.name()) == false) {
					indexOptions.name(index.name());
				}

				// Ψһ�ֶ�
				if(index.unique()) {
					indexOptions.unique(index.unique());
				}

				if(index.sparse()) {
					indexOptions.sparse(index.sparse());
				}

				// ��������
				IndexModel indexModel = new IndexModel(keys,indexOptions);
				// ����
				indexModels.add(indexModel);
			}
		}
		return indexModels;
	}

	@Override
	public Document ekeyObjectToDocument(T t, Boolean updateFlag, String[] ekeys, String... filter) {
		// �ն�����
		if (t == null) {
			return null;
		}

		// ��ȡ���Ͷ���
		Class claz          = t.getClass();

		// ��ȡt���������
		Field[] claz_fields = claz.getDeclaredFields();
		// ��ȡt�����������
		Field[] superFields = superClazz.getDeclaredFields();

		Field[] fields = new Field[claz_fields.length + superFields.length];

		// �ϲ�t��t�����������
		System.arraycopy(claz_fields, 0, fields, 0, claz_fields.length);
		System.arraycopy(superFields, 0, fields, claz_fields.length, superFields.length);

		List<String> excludeParameter = new ArrayList<String>();
		List<String> includeParameter = new ArrayList<String>();
		// ���˵Ĳ���
		if (filter != null && filter.length > 0 && filter[0] != null) {
			excludeParameter = Arrays.asList(filter[0].split(","));
		}
		// ���˵Ĳ���
		if (filter != null && filter.length > 1 && filter[1] != null) {
			includeParameter = Arrays.asList(filter[1].split(","));
		}

		// all key, (name, key)
		Map<String, String> keys = new HashMap<String, String>();
		// �������ض���
		Document document = new Document();

		// ѭ������
		for (Field field: fields) {

			// ObjectId���Բ�����
			if (field.isAnnotationPresent(Index.class)) {
				Index index = field.getAnnotation(Index.class);
				if (index.id() == true) {
					continue;
				}
			}

			// �ֶ�
			String  updateKey      = null;

			// Update annotation
			if (updateFlag != null && updateFlag == true
				&& field.isAnnotationPresent(EKey.class)) {
				EKey update = field.getAnnotation(EKey.class);

				// default value
				updateKey = update.value();

				if (update.priority() == true) {
					updateKey = (updateKey != null || updateKey.startsWith("$") == false) ? null : updateKey;
				}
				else {
					if ((ekeys == null
						|| update.index() == -1
						|| ekeys.length <= update.index()
						|| ekeys[update.index()] == null
						|| ekeys[update.index()].startsWith("$") == false) == false) {
						updateKey = ekeys[update.index()];
					}
				}
			}

 			// ������
			String fieldName = field.getName();

			// ������
			if (excludeParameter.size() > 0 && excludeParameter.contains(fieldName) == true) {
				continue;
			}

			if (includeParameter.size() > 0 && includeParameter.contains(fieldName) == false) {
				continue;
			}

			// ��ȡ�����get������
			String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);


			try {
				// ��ȡget����
				Method method = claz.getMethod(getMethodName);
				// ��ȡget������ֵ
				Object value  = method.invoke(t);

				// continue next operate
				if (value == null) {
					continue;
				}

				// List������
				if (field.getType() == List.class) {
					// ��ȡ����
					ParameterizedType parameter = (ParameterizedType) field.getGenericType();
					// ��ȡ���������
					Type              listType  = parameter.getActualTypeArguments()[0];

					// ��������
					if (listType == Integer.class
							|| listType == Boolean.class
							|| listType == String.class
							|| listType == Double.class
							|| listType == Map.class
							|| listType == List.class
							|| listType == Set.class) {
						// ֱ�Ӽ���ֵ
						if (value != null) {
							if (updateKey == null) {
								keys.put(fieldName, null);
								document.append(fieldName, value);
							}
							else {
								keys.put(fieldName, updateKey);
								document.append(updateKey, new Document().append(fieldName, value));
							}
						}
						continue;
					}

					// ��ȡ���������
					String typeName = listType.toString();
					typeName = typeName.substring(typeName.indexOf("class ")+"class ".length());

					// ʵ����List������
					Object object = Class.forName(typeName).newInstance();

					// ��ȡList���Ͳ������Ͷ�Ӧ��Class����
					Class         listClazz = object.getClass();
					// ��ȡ�����������
					Field[] tFields = listClazz.getDeclaredFields();

					// ��ȡ��List��С
					List<Object>    listValue = (List<Object>) value;
					List<Object>    listMap  = new ArrayList<Object>();

					for (int i=0; i<listValue.size(); i++) {
						// ������Ҫ���ص�ֵ
						Map<String, Object> map = new HashMap<String, Object>();
						// ѭ������
						for (Field field2: tFields) {
							// ObjectId���Բ�����
							if (field2.isAnnotationPresent(Index.class)) {
								Index index = field2.getAnnotation(Index.class);
								if (index.id() == true) {
									continue;
								}
							}

				 			// ������
							String field2Name = field2.getName();
							// ��ȡ�����get������
							String getMethod2Name = "get" + field2Name.substring(0, 1).toUpperCase() + field2Name.substring(1);
							// ��ȡget����
							Method method2 = listClazz.getMethod(getMethod2Name);

							List<Object> list = (List<Object>) value;
							// ��ȡget������ֵ
							Object value2  = method2.invoke(list.get(0));

							// ��Ϊ��ֵ���
 							if (value2 != null) {
								map.put(field2Name, value2);
							}
						}
						listMap.add(map);
					}
					// update operate
					if (updateFlag  != null
						&& updateFlag == true
						&& updateKey   != null) {
						keys.put(fieldName, updateKey);

						Document documentValue = (Document) document.get(updateKey);
						if (documentValue == null) { // first
							document.append(updateKey, new Document().append(fieldName, listMap));
						}
						else {
							documentValue.append(fieldName, listMap);
							document.append(updateKey, documentValue);
						}
					}
					// not update
					else {
						keys.put(fieldName, null);
						document.append(fieldName, listMap);
					}
					continue;
				}

				if (value != null) {
					if (updateFlag  != null
						&& updateFlag == true
						&& updateKey   != null) {
						keys.put(fieldName, updateKey);

						Document documentValue = (Document) document.get(updateKey);
						if (documentValue == null) { // first
							document.append(updateKey, new Document().append(fieldName, value));
						}
						else {
							documentValue.append(fieldName, value);
							document.append(updateKey, documentValue);
						}
					}
					else {
						keys.put(fieldName, null);
						document.append(fieldName, value);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		Document result = new Document();

		// class Update annotation
		if ( updateFlag != null
				&& updateFlag == true
				&& claz.isAnnotationPresent(EKey.class) == true
			) {
			EKey update = (EKey) claz.getAnnotation(EKey.class);

			// default value
			String updateKey = update.value();

			if (update.priority() == true) {
				updateKey = (updateKey != null || updateKey.startsWith("$") == false) ? null : updateKey;
			}
			else {
				if ((ekeys == null
					|| update.index() == -1
					|| ekeys.length <= update.index()
					|| ekeys[update.index()] == null
					|| ekeys[update.index()].startsWith("$") == false) == false) {
					updateKey = ekeys[update.index()];
				}
			}

			if(updateKey != null
				&& updateKey.length() > 1) {
				// keys cycle
				for (Entry<String, String> entry: keys.entrySet()) {
					String value = entry.getValue();
					if (value != null
						&& document.get(value) != null
						&& value.startsWith("$") == true ) {
						// update $,for example $set
						if (value.equals(updateKey) == true) {
							Document temp = (Document) document.get(value);
							for (String set: temp.keySet()) {
								document.append(set, temp.get(set));
							}
							document.remove(value);
						}
						else {
							document.append(value, document.get(value));
							document.remove(value);
						}
					}
				}

				if (document.size() > 0) {
					result.append(updateKey, document);
				}
			}
		}
		else {
			result = document;
		}

		return result;
	}

	@Override
	public Document objectToDocument (T t, String... filter) {
		// �ն�����
		if (t == null) {
			return null;
		}

		// ��ȡ���Ͷ���
		Class claz          = t.getClass();

		// ��ȡt���������
		Field[] claz_fields = claz.getDeclaredFields();
		// ��ȡt�����������
		Field[] superFields = superClazz.getDeclaredFields();

		Field[] fields = new Field[claz_fields.length + superFields.length];

		// �ϲ�t��t�����������
		System.arraycopy(claz_fields, 0, fields, 0, claz_fields.length);
		System.arraycopy(superFields, 0, fields, claz_fields.length, superFields.length);

		List<String> excludeParameter = new ArrayList<String>();
		List<String> includeParameter = new ArrayList<String>();
		// ���˵Ĳ���
		if (filter != null && filter.length > 0 && filter[0] != null) {
			excludeParameter = Arrays.asList(filter[0].split(","));
		}
		// ���˵Ĳ���
		if (filter != null && filter.length > 1 && filter[1] != null) {
			includeParameter = Arrays.asList(filter[1].split(","));
		}

		// �������ض���
		Document document = new Document();

		// ѭ������
		for (Field field: fields) {
			// ObjectId���Բ�����
			if (field.isAnnotationPresent(Index.class)) {
				Index index = field.getAnnotation(Index.class);
				if (index.id() == true) {
					continue;
				}
			}

			// ������
			String fieldName = field.getName();

			// ������
			if (excludeParameter.size() > 0 && excludeParameter.contains(fieldName) == true) {
				continue;
			}
			if (includeParameter.size() > 0 && includeParameter.contains(fieldName) == false) {
				continue;
			}

			// ��ȡ�����get������
			String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

			try {
				// ��ȡget����
				Method method = claz.getMethod(getMethodName);
				// ��ȡget������ֵ
				Object value  = method.invoke(t);

				// continue next operate
				if (value == null) {
					continue;
				}

				// List������
				if (field.getType() == List.class) {
					// ��ȡ����
					ParameterizedType parameter = (ParameterizedType) field.getGenericType();
					// ��ȡ���������
					Type              listType  = parameter.getActualTypeArguments()[0];

					// ��������
					if (listType == Integer.class
							|| listType == Boolean.class
							|| listType == String.class
							|| listType == Double.class
							|| listType == Map.class
							|| listType == List.class
							|| listType == Set.class) {
						// ֱ�Ӽ���ֵ
						if (value != null) {
							document.append(fieldName, value);
						}
						continue;
					}

					// ��ȡ���������
					String typeName = listType.toString();
					typeName = typeName.substring(typeName.indexOf("class ")+"class ".length());

					// ʵ����List������
					Object object = Class.forName(typeName).newInstance();

					// ��ȡList���Ͳ������Ͷ�Ӧ��Class����
					Class         listClazz = object.getClass();
					// ��ȡ�����������
					Field[] tFields = listClazz.getDeclaredFields();

					// ��ȡ��List��С
					List<Object>    listValue = (List<Object>) value;
					List<Object>    listMap  = new ArrayList<Object>();

					for (int i=0; i<listValue.size(); i++) {
						// ������Ҫ���ص�ֵ
						Map<String, Object> map = new HashMap<String, Object>();
						// ѭ������
						for (Field field2: tFields) {
							// ObjectId���Բ�����
							if (field2.isAnnotationPresent(Index.class)) {
								Index index = field2.getAnnotation(Index.class);
								if (index.id() == true) {
									continue;
								}
							}

							// ������
							String field2Name = field2.getName();
							// ��ȡ�����get������
							String getMethod2Name = "get" + field2Name.substring(0, 1).toUpperCase() + field2Name.substring(1);
							// ��ȡget����
							Method method2 = listClazz.getMethod(getMethod2Name);

							List<Object> list = (List<Object>) value;
							// ��ȡget������ֵ
							Object value2  = method2.invoke(list.get(0));

							// ��Ϊ��ֵ���
							if (value2 != null) {
								map.put(field2Name, value2);
							}
						}
						listMap.add(map);
					}
					document.append(fieldName, listMap);
					continue;
				}

				if (value != null) {
					document.append(fieldName, value);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return document;
	}

	@Override
	public List<Document> ekeyObjectsToDocuments (List<T> ts, Boolean updateFlag, String[] ekeys, String... filters) {
		// �ն�����
		if (ts == null) {
			return null;
		}

		// ��Ҫ���صĶ���
		List<Document> list = new ArrayList<Document>();

		// ѭ������
		Iterator iterator = ts.iterator();

		while (iterator.hasNext()) {
			T t = (T) iterator.next();
			Document document = ekeyObjectToDocument(t, updateFlag, ekeys, filters);
			list.add(document);
		}
		return list;
	}

	@Override
	public T documentToObject (Document document, String... filter) {
		// �ն�����
		if (document == null) {
			return null;
		}

		// ��ȡ��
		Set<String> keys = document.keySet();

		// ��������
		T t;
		try {
			t = (T) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		List<String> excludeParameter = new ArrayList<String>();
		List<String> includeParameter = new ArrayList<String>();
		// ���˵Ĳ���
		if (filter != null && filter.length > 0 && filter[0] != null) {
			excludeParameter = Arrays.asList(filter[0].split(","));
		}
		// ���˵Ĳ���
		if (filter != null && filter.length > 1 && filter[1] != null) {
			includeParameter = Arrays.asList(filter[1].split(","));
		}

		// ��ȡT����
		Field[] clazz_fields   = clazz.getDeclaredFields();
		// ��ȡT�����������
		Field[] superFields    = superClazz.getDeclaredFields();

		Field[] fields = new Field[clazz_fields.length + superFields.length];

		// �ϲ�T��T�����������
		System.arraycopy(clazz_fields, 0, fields, 0, clazz_fields.length);
		System.arraycopy(superFields, 0, fields, clazz_fields.length, superFields.length);

		// ��ѭ��
		for (String str : keys) {

			if (excludeParameter.size() > 0 && excludeParameter.contains(str) == true) {
				continue;
			}
			if (includeParameter.size() > 0 && includeParameter.contains(str) == false) {
				continue;
			}

			// ����ѭ��
			for (Field field: fields) {
				//������
				String fieldName = field.getName();

				// set������
				String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

				// ������ȡset����
				Method setMethod;
				try {
					// ��ȡset����
					setMethod = clazz.getMethod(setMethodName, field.getType());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				// ===================��ȡObjectId================
				// Indexע��
				if (field.isAnnotationPresent(Index.class)) {
					// ��ȡIndexע��
					Index index = field.getAnnotation(Index.class);
					if (index.id() == true && field.getType() == String.class) {
						try {
							// ��ȡObjectId
							setMethod.invoke(t, document.get("_id").toString());
						}  catch (Exception e) {
							throw new RuntimeException(e);
						}
						continue;
					}
				}

				// �ֶ���������ͬ�ж�
				if (!fieldName.equalsIgnoreCase(str)) {
					continue;
				}

				// ��ȡ�����ֶ�ֵ
				try {
					// �жϷ��ض��������,��ȡ��(�ֶ�)��Ӧ��ֵ,������Ӧ��set����
					if (field.getType() == Integer.class) {
						setMethod.invoke(t, document.getInteger(str));
					}
					else  if(field.getType() == Double.class) {
						setMethod.invoke(t, document.getDouble(str));
					}
					else if (field.getType() == Long.class) {
						setMethod.invoke(t, document.getLong(str));
					}
					else if (field.getType() == Boolean.class) {
						setMethod.invoke(t, document.getBoolean(str));
					}
					else if (field.getType() == Date.class) {
						setMethod.invoke(t, document.getDate(str));
					}
					else if (field.getType() == String.class) {
						setMethod.invoke(t, document.getString(str));
					}
					else if (field.getType() == List.class) {
						// ��ȡList<T>��T����
						ParameterizedType types = (ParameterizedType) field.getGenericType();
						Type           listType = types.getActualTypeArguments()[0];
						// ��ȡֵ
						Object        listValue = document.get(str, List.class);

						// List<T>����Ϊ��������
						if (listType == Integer.class
								|| listType == Boolean.class
								|| listType == Double.class
								|| listType == Map.class
								|| listType == List.class
								|| listType == Set.class)
						{
							setMethod.invoke(t, listValue);
							continue;
						}

						// ��ȡ������T��
						String listTypeName = listType.toString();
						listTypeName        = listTypeName.substring(listTypeName.indexOf("class ")+"class ".length());

						// ʵ����List������
						Object object = Class.forName(listTypeName).newInstance();

						// ��ȡList���Ͳ������Ͷ�Ӧ��Class����
						Class         listTypeClazz = object.getClass();
						// ��ȡ�����������
						Field[] tFields = listTypeClazz.getDeclaredFields();

						// �洢��ȡת��֮���ֵ
						List<Object> lists = new ArrayList<Object>();

						// ֵ
						List<Object> valueList = (List<Object>) listValue;

						// ѭ��,Documentת��ΪT
						for (int i=0; i<valueList.size(); i++) {
							// �������������
							Object ts = Class.forName(listTypeName).newInstance();

							// 1.Document����
							if (valueList.get(i).getClass() == Document.class) {
								for (Field field2 : tFields) {
									String field2Name = field2.getName();
									Object field2Value = ((Document) valueList.get(i)).get(field2Name);

									// set����
									String tsSetMethodName = "set"+field2Name.substring(0, 1).toUpperCase()+field2Name.substring(1);
									Method tsMethod = listTypeClazz.getMethod(tsSetMethodName, field2.getType());
									tsMethod.invoke(ts, field2Value);
								}
								lists.add(ts);
							}
							// 2. Map����
							else if (valueList.get(i).getClass() == Map.class) {
								// ȡֵ
								Map<String, Object> mapValue = (Map<String, Object>) valueList.get(i);
								// ѭ��
								for (Field field2 : tFields) {
									String field2Name = field2.getName();
									Object field2Value = mapValue.get(field2Name);

									// set����
									String tsSetMethodName = "set"+field2Name.substring(0, 1).toUpperCase()+field2Name.substring(1);
									Method tsMethod = listTypeClazz.getMethod(tsSetMethodName, field2.getType());
									tsMethod.invoke(ts, field2Value);
								}
								lists.add(ts);
							}
							else {
								//�������ֱ��ȡֵ
								lists.add(valueList.get(i));
							}
						}
						setMethod.invoke(t, lists);
					}
					else if (field.getType() == Map.class){
						// ȡֵ
						Object fieldValue = document.get(str);

						Map<String, Object> map = new HashMap<String,Object>();
						// 1.����Document����
						if (fieldValue.getClass() == Document.class) {
							Document document2 = (Document) fieldValue;
							for (String docKey: document2.keySet()) {
								map.put(docKey, document2.get(docKey));
							}
							setMethod.invoke(t, map);
						}
						// 2.����Map��
						else {
							setMethod.invoke(t, document.get(str, Map.class));
						}
					}

				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
		}

		// ����ѭ��
		for (Field field: fields) {
			//������
			String fieldName = field.getName();

			// set������
			String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

			// ������ȡset����
			Method setMethod;
			try {
				// ��ȡset����
				setMethod = clazz.getMethod(setMethodName, field.getType());
				// Index Annotation
				if (field.isAnnotationPresent(Index.class)) {
					Index index = field.getAnnotation(Index.class);
						// already setting default value
						if(index.omission() == true) {
							if (field.getType()     == Integer.class && document.getInteger(field.getName()) == null) {
								setMethod.invoke(t, 0);
							}
							else if (field.getType() == Double.class && document.getDouble(field.getName())   == null) {
								setMethod.invoke(t, 0.00);
							}
							else if (field.getType() == Boolean.class && document.getBoolean(field.getName()) == null) {
								setMethod.invoke(t, false);
							}
							else if (field.getType() == Long.class && document.getLong(field.getName())       == null) {
								setMethod.invoke(t, 0L);
							}
							else {
							}
						}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// �ж��Ƿ����ObjectId
		return t;
	}

	@Override
	public List<T> documentsToObjects (List<Document> listDocuments, String... filters) {
		// �ն�����
		if (listDocuments == null || listDocuments.size() == 0) {
			return null;
		}

		// �������صĶ���
		List<T> ts = new ArrayList<T>();

		// ��ȡѭ������
		Iterator<Document> iterator = listDocuments.iterator();
		// ѭ������
		while (iterator.hasNext()) {
			// ��ȡDocument����
			Document document = iterator.next();
			// Documentת��ΪT����
			T t = documentToObject(document, filters);

			ts.add(t);
		}
		return ts;
	}

	@Override
	public Document filterDocument (Document document, String... filters) {
		// �ն�����
		if (document == null) {
			return null;
		}
		// all key-value
		Set<Entry<String, Object>> set = document.entrySet();
		// cycle
		Iterator<Entry<String, Object>> iterator = set.iterator();

		List<String> excludeParameter = new ArrayList<String>();
		List<String> includeParameter = new ArrayList<String>();
		List<String> timesParameter = new ArrayList<String>();

		// exclude parameter
		if (filters != null && filters.length > 0 && filters[0] != null) {
			excludeParameter = Arrays.asList(filters[0].split(","));
		}
		// include parameter
		if (filters != null && filters.length > 1 && filters[1] != null) {
			includeParameter = Arrays.asList(filters[1].split(","));
		}
		// include parameter
		if (filters != null && filters.length > 2 && filters[2] != null) {
			timesParameter = Arrays.asList(filters[2].split(","));
		}

		Document resultDocument = new Document();

		while (iterator.hasNext()) {
			// key-value
			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();

			// key
			String key = entry.getKey();

			// excludes
			if (excludeParameter.size() > 0 && excludeParameter.contains(key) == true) {
				continue;
			}
			// includes
			if (includeParameter.size() > 0 && includeParameter.contains(key) == false) {
				continue;
			}

			Boolean timeFlag = false;
			// deal with time
			if (timesParameter.size() > 0) {
				Iterator timeIterator = timesParameter.iterator();
				while (timeIterator.hasNext()) {
					String regex_time = (String) timeIterator.next();
					if (key != null
							&& regex_time != null
							&& key.matches(".*"+ regex_time+".*") == true) {
						timeFlag = true;
						break;
					}
				}
			}

			// deal with time
			if (timeFlag == true) {
				resultDocument.append(key, Tool.dateToString(entry.getValue(), "yyyy-MM-dd HH:mm:ss"));
			}
			else {
				resultDocument.append(key, entry.getValue());
			}
		}

		return resultDocument;
	}

	@Override
	public List<Document> filterDocuments (List<Document> listDocuments, String... filters) {
		// �ն�����
		if (listDocuments == null || listDocuments.size() == 0) {
			return null;
		}

		// �������صĶ���
		List<Document> documents = new ArrayList<Document>();

		// ��ȡѭ������
		Iterator<Document> iterator = listDocuments.iterator();
		// ѭ������
		while (iterator.hasNext()) {
			// get document
			Document document = iterator.next();
			document = filterDocument(document, filters);
			
			// add it to document list
			documents.add(document);
		}
		return documents;
	}
	
	@Override
	public Bson mapToBson (Map<String, Object> target) {
		if (target == null) {
			return null;
		}
		
		Iterator iterator = target.entrySet().iterator();
		BasicDBObject bson = new BasicDBObject ();
		
		// cycle 
		while (iterator.hasNext()) {
			Entry entry  = (Entry) iterator.next();
			// key
			String key   = (String) entry.getKey();
			// value
			Object value = entry.getValue();
			
			// if it Map Object
			if (value instanceof Map) {
				BasicDBObject result = new BasicDBObject();
				Map<String, Object> map = (Map<String, Object>) value;
				
				// cycle all keys
				for (String valueKey: map.keySet()) {
					// add it to result
					result.append(valueKey, map.get(valueKey));
				}
				bson.put(key, result);
			}
			else {
				bson.put(key, value);
			}
		}
		
		return bson;
	}
}
