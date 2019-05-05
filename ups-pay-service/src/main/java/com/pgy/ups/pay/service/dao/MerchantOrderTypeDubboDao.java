package com.pgy.ups.pay.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pgy.ups.pay.interfaces.entity.MerchantOrderTypeEntity;

@Repository
public interface MerchantOrderTypeDubboDao extends JpaRepository<MerchantOrderTypeEntity, Long>{
    
	@Modifying
	@Transactional
	@Query(value="DELETE FROM ups_t_merchant_order_type WHERE id=?1",nativeQuery=true)
	void deleteMerchantById(Long id);
    
   
}
