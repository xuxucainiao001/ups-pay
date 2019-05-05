package com.pgy.ups.pay.commom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pgy.ups.pay.interfaces.entity.CollectChooseEntity;

public interface CollectChooseDao extends JpaRepository<CollectChooseEntity, Long> {

	@Query("from CollectChooseEntity e where e.merchantCode=?1 and e.active=true")
	CollectChooseEntity queryByMerchantName(String merchantCode);

}
