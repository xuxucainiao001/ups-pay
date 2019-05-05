package com.pgy.ups.pay.interfaces.query;

import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;

/**
 * 订单查询第三方接口
 * 
 * @author 墨凉
 *
 */
public interface OrderQueryService<T> {

	/**
	 * 多订单查询
	 */
	void doMultiQuery();

	/**
	 * 单个订单查询
	 * 
	 * @param ope
	 * @param queryOnly 查询结果后是否更新数据库
	 * @return
	 */
	T doSingleQuery(OrderPushEntity ope, boolean queryOnly) throws BussinessException;

}
