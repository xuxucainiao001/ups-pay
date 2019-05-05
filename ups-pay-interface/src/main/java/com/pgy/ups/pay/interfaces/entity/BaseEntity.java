package com.pgy.ups.pay.interfaces.entity;

import com.pgy.ups.pay.interfaces.model.Model;


import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity extends Model {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6386865233011450261L;

	/**
	 * 
	 */


    @Column(name="update_time")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date updateTime;

    @Column(name = "create_user")
    protected String createUser;


    @Column(name = "update_user")
    protected String updateUser;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

}
