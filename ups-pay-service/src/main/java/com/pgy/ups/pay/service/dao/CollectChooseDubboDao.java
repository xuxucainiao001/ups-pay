package com.pgy.ups.pay.service.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.CollectChooseEntity;
import com.pgy.ups.pay.interfaces.form.CollectChooseForm;

@Repository
public interface CollectChooseDubboDao extends JpaRepository<CollectChooseEntity, Long>{
    
	@Query(nativeQuery=true,value="SELECT * FROM ups_t_collect_choose WHERE IF(:#{#form.merchantCode}!='',merchant_code=:#{#form.merchantCode},1=1) AND IF(:#{#form.collectType}!='',collect_type=:#{#form.collectType},1=1) ORDER BY create_time DESC")
	Page<CollectChooseEntity> queryByForm(@Param("form")CollectChooseForm form,Pageable pageable);

}
