package com.pgy.ups.pay.commom.dao;

import com.pgy.ups.pay.interfaces.entity.UpsPayChannelBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UpsPayChannelBankDao  extends JpaRepository<UpsPayChannelBankEntity,Long> {

    @Query(value = "select  * FROM ups_t_bank t1,ups_t_pay_channel_bank t2 where t1.id = t2.ups_bank_id  and t2.pay_channel = ?1  and t1.bank_code = ?2", nativeQuery = true)
    UpsPayChannelBankEntity queryUpsPayChannelBank(String payChannel,String bankCode);


}
