package com.pgy.ups.pay.interfaces.model;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;




/**
 * 借款代付
 * 
 * @author 墨凉
 *
 */
public class UpsPayParamModel extends UpsParamModel {
    
	/**
	 * 验签顺序
	 */
	private static final String[] SIGN_RULE = new String[] { "merchantCode", "userNo", "realName", "bankCard", "identity",
			"bankCode", "phoneNo", "remark", "businessType", "businessFlowNum", "amount" };

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919779500406118619L;

	/* 金额 */
	@NotNull(message = "金额不能为空")
	@Min(value = 0, message = "金额必须大于{value}")
	private BigDecimal amount;

	

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}



	@Override
	public String[] getSignRule() {
		return UpsPayParamModel.SIGN_RULE;
	}

}
