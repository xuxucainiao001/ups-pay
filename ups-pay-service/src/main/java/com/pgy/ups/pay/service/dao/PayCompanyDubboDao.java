package com.pgy.ups.pay.service.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.PayCompanyEntity;

@Repository
public interface PayCompanyDubboDao extends JpaRepository<PayCompanyEntity, Long>{
    
	@Query(nativeQuery=true,value="SELECT * FROM ups_t_route_pay_company t WHERE IF(?1 !='',t.company_name LIKE %?1%  ,1=1) AND IF(?2 !='',?2 = t.company_code,1=1)")
	Page<PayCompanyEntity> findByCondition(String companyName, String companyCode,Pageable pageable);
	
	Page<PayCompanyEntity> findByCompanyNameLikeAndCompanyCode(String companyName, String companyCode,Pageable pageable);

}
