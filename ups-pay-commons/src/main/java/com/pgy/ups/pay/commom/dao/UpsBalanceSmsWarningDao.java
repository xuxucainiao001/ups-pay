package com.pgy.ups.pay.commom.dao;


import com.pgy.ups.pay.interfaces.entity.UpsBalanceSmsConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpsBalanceSmsWarningDao extends JpaRepository<UpsBalanceSmsConfigEntity, Long> {
}
