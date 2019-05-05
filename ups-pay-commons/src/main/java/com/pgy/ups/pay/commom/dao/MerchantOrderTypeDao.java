package com.pgy.ups.pay.commom.dao;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.MerchantOrderTypeEntity;

@Repository
public interface  MerchantOrderTypeDao extends JpaRepository<MerchantOrderTypeEntity, Long>{
    
	@Query("FROM MerchantOrderTypeEntity e WHERE e.merchantConfigEntity.merchantCode=?1 AND e.upsOrderTypeEntity.orderType=?2 AND (?3 BETWEEN  e.startTime AND e.endTime)")
	Optional<MerchantOrderTypeEntity> confirmMerchantOrderType(String merchantCode, String orderType, Date date);

}
