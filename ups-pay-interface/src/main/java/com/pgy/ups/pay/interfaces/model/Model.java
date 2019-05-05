package com.pgy.ups.pay.interfaces.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 顶层实体类抽象类
 * @author 墨凉
 *
 */
public abstract class Model implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -944119455239662991L;

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
	
}