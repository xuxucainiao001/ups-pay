package com.pgy.ups.pay.service.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.form.UpsUserSignForm;

@Repository
public interface UpsUserSignDubboDao extends JpaRepository<UpsAuthSignEntity, Long> {

	@Query(value = " select  * FROM ups_t_user_sign c  WHERE c.merchant_code = :#{#form.merchantCode} AND IF (:#{#form.signType} !='',c.sign_type = :#{#form.signType}  ,1=1) AND IF (:#{#form.bankMd5} !='',c.bank_md5 = :#{#form.bankMd5} ,1=1) AND IF (:#{#form.realNameMd5} !='',c.real_name_md5 = :#{#form.realNameMd5} ,1=1) AND IF (:#{#form.phoneNoMd5} !='',c.phone_no_md5 = :#{#form.phoneNoMd5} ,1=1) AND IF (:#{#form.identityMd5} !='',c.identity_md5 = :#{#form.identityMd5} ,1=1) AND IF (:#{#form.businessFlowNum} !='',c.business_flow_num = :#{#form.businessFlowNum} ,1=1)  AND IF (:#{#form.PayChannel} !='',c.pay_channel = :#{#form.PayChannel} ,1=1) AND IF (:#{#form.tradeNo} !='',c.trade_no = :#{#form.tradeNo} ,1=1) AND IF (:#{#form.status} !='',c.status = :#{#form.status} ,1=1) ORDER BY c.create_time desc", nativeQuery = true)
	Page<UpsAuthSignEntity> queryByForm(@Param("form") UpsUserSignForm form, Pageable pageable);

}
