package com.pgy.ups.pay.interfaces.entity;


import com.pgy.ups.pay.interfaces.model.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="`ups_t_sign_default_config`")
public class UpsSignDefaultConfigEntity  extends Model {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4423139508757459276L;

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="merchant_code")
    private  String merchantCode;



    @Column(name="tpp_mer_No")
    private String tppMerNo;

    @Column(name="sign_type")
    private  String signType;

    @Column(name="create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name="update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;


    public String getMerchantCodm() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }



    public String getTppMerNo() {
        return tppMerNo;
    }

    public void setTppMerNo(String tppMerNo) {
        this.tppMerNo = tppMerNo;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

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
}
