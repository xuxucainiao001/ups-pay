package com.pgy.ups.pay.commom.dao;


import com.pgy.ups.pay.interfaces.entity.UpsBalanceUserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpsBalanceUserGroupDao extends JpaRepository<UpsBalanceUserGroupEntity, Long> {

    List<UpsBalanceUserGroupEntity> findByGroupCodeAndTppMerNo(String code, String tppMerNo);
}
