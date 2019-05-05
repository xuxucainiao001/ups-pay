package com.pgy.ups.pay.route.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;

@Repository
public interface PayCompanyDao extends JpaRepository<PayCompanyEntity, Long>{
    
	@Query("select c from PayCompanyEntity c where c.active=true")
	List<PayCompanyEntity> queryAllAvailablePayChannels();

}
