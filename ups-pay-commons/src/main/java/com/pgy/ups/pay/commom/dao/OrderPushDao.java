package com.pgy.ups.pay.commom.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;

public interface OrderPushDao extends JpaRepository<OrderPushEntity, Long>{
    
	//如果requery字段为真，需要强行查询一次无视订单状态和订单时间
	@Query("from OrderPushEntity o where o.payChannel=?1 and o.orderType=?2 and (o.createTime > ?3 and o.nextQueryTime< NOW() and o.orderStatus in ('new','dispose','error')) or o.requery=true")
	List<OrderPushEntity> queryByNeedOrderQueryList(String payChannel, String orderType,
			Date days);
    
	@Query(nativeQuery=true,value="select * from ups_t_order_push o where o.push_status IN('1','2')")
	List<OrderPushEntity>  queryByNeedOrderPushList();
    
	@Modifying
	@Query(nativeQuery=true,value="update ups_t_order_push o set o.query_count=0,o.next_query_time=now() where o.order_id=?1")
	void resetOrderQueryTimeAndCount(Long id);

	OrderPushEntity queryByOrderId(Long upsOrderId);

}
