package com.pgy.ups.pay.commom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.UpsSmsChannelEntity;

@Repository

public interface UpsSmsChannelDao extends JpaRepository<UpsSmsChannelEntity, Long> {


    UpsSmsChannelEntity findBySmsUserfulCode(String code);
}
