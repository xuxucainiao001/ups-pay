package com.pgy.ups.pay.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.OrderPushEntity;

@Repository
public interface UpsOrderPushDubboDao extends JpaRepository<OrderPushEntity, Long>{

	OrderPushEntity queryByOrderId(Long id);

}
