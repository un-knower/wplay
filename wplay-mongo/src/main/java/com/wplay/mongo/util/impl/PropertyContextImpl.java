package com.wplay.mongo.util.impl;

import com.wplay.mongo.util.PropertyContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 
 * @author         lolog
 * @version        V1.0  
 * @Date           2016��7��6��
 * @Company        CIMCSSC
 * @Description    ���ñ�������
*/
@Component("propertyContext")
public class PropertyContextImpl implements PropertyContext {

	@Value("#{conf.expiry}") // Cookie expire time
	private Integer expiry;

	@Value("#{conf.admin}")  // admin name
	private String admin;
	@Value("#{conf.home}")   // home name
	private String home;
	@Value("#{conf.layout_default}") // layout
	private String layoutDefault;
	@Value("#{conf.admin_layout}")   // admin layout path
	private String adminLayout;
	@Value("#{conf.mongo_depr}")     // table separator
	private String db_depr;
	@Value("#{conf.mongo_prefix}")   // table prefix
	private String db_prefix;
	@Value("#{conf.regex_time}")     // table prefix
	private String regex_time;
	@Value("#{conf.uploadPath}")
	private String uploadPath;

	public PropertyContextImpl() {

	}

	@Override
	public Integer getExpiry() {
		expiry = (expiry == null) ? 3600 : expiry;
		return expiry;
	}
	@Override
	public String getAdmin() {
		// Ĭ��ΪadminĿ¼
		admin = (admin == null) ? "admin" : admin;
		// �Ƴ���һ��"/"����
		admin = (admin.indexOf("/") == 0) ? admin.substring(1) : admin;
		// �Ƴ����һ��"/"����
		admin = (admin.lastIndexOf("/") == (admin.length() - 1) && admin.length() > 0) ?  admin.substring(0, admin.lastIndexOf("/")) : admin;

		return admin;
	}
	@Override
	public String getHome() {
		// Ĭ��Ϊ��Ŀ¼
		home = (home == null) ? "" : home;
		// �Ƴ���һ��"/"����
		home = (home.indexOf("/") == 0) ? admin.substring(1) : home;
		// �Ƴ����һ��"/"����
		home = (home.lastIndexOf("/") == (home.length() - 1) && home.length() > 0) ? home.substring(0, home.lastIndexOf("/")) : home;
		return home;
	}
	
	@Override
	public String getLayoutDefault () {
		layoutDefault = (layoutDefault == null) ? "default" : layoutDefault;
		return layoutDefault;
	}
	
	@Override
	public String getAdminLayout() {
		adminLayout = (adminLayout == null) ? "admin/view/layout" : adminLayout;
		return adminLayout;
	}
	
	@Override
	public String db_depr () {
		return db_depr;
	}
	
	@Override
	public String db_prefix() {
		return db_prefix;
	}
	@Override
	public String regex_time() {
		return regex_time;
	}
	@Override
	public String uploadPath() {
		return uploadPath;
	}
}
