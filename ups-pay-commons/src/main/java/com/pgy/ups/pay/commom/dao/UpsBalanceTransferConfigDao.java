package com.pgy.ups.pay.commom.dao;

import com.pgy.ups.pay.interfaces.entity.UpsBalanceTransferConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpsBalanceTransferConfigDao  extends JpaRepository<UpsBalanceTransferConfigEntity, Long> {
}
