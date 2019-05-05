package com.pgy.ups.pay.service.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.UpsBankEntity;

@Repository
public interface BankConfigDubboDao extends JpaRepository<UpsBankEntity, Long> {

	@Query(value = "SELECT * FROM ups_t_bank e WHERE IF (?1 != '',e.bank_code =?1,1=1) AND IF (?2 !='',e.bank_name LIKE %?2% ,1=1) ORDER BY e.id DESC",  nativeQuery = true)
	Page<UpsBankEntity> findByForm(String bankCode, String bankName,Pageable pageable);

}
