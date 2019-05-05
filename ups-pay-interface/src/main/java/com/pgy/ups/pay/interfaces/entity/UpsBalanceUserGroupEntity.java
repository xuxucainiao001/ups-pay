package com.pgy.ups.pay.interfaces.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="ups_t_balance_user_group")
public class UpsBalanceUserGroupEntity extends BaseEntity {


    /**
	 * 
	 */
	private static final long serialVersionUID = -4785484671515602490L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="user_name")
    private String userName;


    @Column(name="group_code")
    private String groupCode;


    @Column(name="user_phone")
    private  String userPhone;

    @Column(name="tpp_mer_no")
    private String  tppMerNo;

    @Column(name="remakes")
    private String  remakes;


    @Column(name = "is_delete",columnDefinition="BIT")
    private Boolean isDelete;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getTppMerNo() {
        return tppMerNo;
    }

    public void setTppMerNo(String tppMerNo) {
        this.tppMerNo = tppMerNo;
    }

    public String getRemakes() {
        return remakes;
    }

    public void setRemakes(String remakes) {
        this.remakes = remakes;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }



}
