package com.wplay.mongo.model;

import com.wplay.mongo.annotations.EKey;
import com.wplay.mongo.annotations.Index;
import com.wplay.mongo.model.base.Base;

/**
 * @author lolog
 * @version V1.0
 * @Date 2016.08.17
 * @Company WEIGO
 * @Description currency model
 */
@EKey("$set")
public class Currency extends Base {
	@Index(unique = true)
	private String currency_id;
	@Index(unique = true)
	private String name;
	private Double rate;

	public Currency() {
	}

	public String getCurrency_id() {
		return currency_id;
	}
	public void setCurrency_id(String currency_id) {
		this.currency_id = currency_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "Currency [currency_id=" + currency_id + ", name=" + name + ", rate=" + rate + ", Base [id=" + getId()
				+ ", addy_id=" + getAddy_id() + ", add_time=" + getAdd_time() + ", editors=" + getEditors() 
				+ ", is_del=" + getIs_del() + ", is_forbidden=" + getIs_forbidden() + "]]";
	}
}
