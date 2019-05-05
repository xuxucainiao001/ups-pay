package com.pgy.ups.pay.commom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.UpsLogEntity;


@Repository
public interface UpsLogDao extends JpaRepository<UpsLogEntity, Long>{

}
