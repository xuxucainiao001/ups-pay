package com.pgy.ups.pay.commom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.UpsSignDefaultConfigEntity;

@Repository
public interface UpsSignDefaultConfigDao extends JpaRepository<UpsSignDefaultConfigEntity, Long> {


}
