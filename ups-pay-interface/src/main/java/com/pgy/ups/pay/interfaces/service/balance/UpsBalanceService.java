package com.pgy.ups.pay.interfaces.service.balance;

import com.pgy.ups.pay.interfaces.entity.UpsBalanceEntity;

public interface UpsBalanceService {

	void saveEntity(UpsBalanceEntity upsBalanceEntity);

	void updateEntity(UpsBalanceEntity upsBalanceEntity);

	UpsBalanceEntity getBalanceByMemberId(String memberId);
}
