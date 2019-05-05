package com.pgy.ups.pay.interfaces.form;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.pgy.ups.pay.interfaces.model.Model;




@SuppressWarnings("serial")
public abstract class BaseForm extends Model {

	private int pageNum = 1;

	private int pageSize = 10;


	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Pageable getPageRequest(){
		return  PageRequest.of(pageNum - 1,pageSize);
	}

}
