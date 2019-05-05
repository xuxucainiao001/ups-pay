package com.pgy.ups.pay.service.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pgy.ups.pay.interfaces.entity.MerchantConfigEntity;
import com.pgy.ups.pay.interfaces.form.MerchantConfigForm;

@Repository
public interface MerchantConfigDubboDao extends JpaRepository<MerchantConfigEntity, Long> {

	@Query(value = "SELECT * FROM ups_t_merchant_config c  WHERE IF (?1 != '',c.merchant_code =?1,1=1) AND IF (?2 !='',c.merchant_name LIKE %?2% ,1=1)  AND IF (?3 !='',c.description LIKE %?3% ,1=1) ORDER BY c.id DESC", nativeQuery = true)
	Page<MerchantConfigEntity> findByForm(String merchantCode, String merchantName, String description,
			Pageable pageable);

	@Modifying
	@Transactional
	@Query("UPDATE MerchantConfigEntity e SET e.available = ?2 WHERE e.id=?1")
	void enableOrDisableMerchantConfig(Long id, boolean available);

	@Modifying
	@Transactional
	@Query("UPDATE MerchantConfigEntity e SET e.merchantCode=:#{#form.merchantCode},e.merchantName=:#{#form.merchantName},e.description=:#{#form.description},e.merchantPublicKey=:#{#form.merchantPublicKey},e.upsPrivateKey=:#{#form.upsPrivateKey},e.updateUser=:#{#form.updateUser},e.updateTime=NOW() WHERE e.id=:#{#form.id}")
	void updateMerchantConfig(@Param("form") MerchantConfigForm form);

	MerchantConfigEntity queryByMerchantCode(String merchantCode);

	MerchantConfigEntity queryByMerchantName(String merchantName);


}
