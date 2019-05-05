package com.pgy.ups.pay.commom.service.impl;

import com.pgy.ups.pay.commom.dao.UpsBalanceSmsWarningDao;
import com.pgy.ups.pay.interfaces.entity.*;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceService;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceSmsRecordService;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceSmsWarningService;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceUserGroupService;
import com.pgy.ups.pay.interfaces.service.sms.UpsSmsChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service
public class UpsBalanceSmsWarningServiceImpl implements UpsBalanceSmsWarningService {

    private Logger logger = LoggerFactory.getLogger(UpsBalanceSmsWarningServiceImpl.class);


    @Resource
    private UpsBalanceSmsWarningDao upsBalanceSmsWarningDao;

    @Resource
    private UpsBalanceService upsBalanceService;

    @Resource
    private UpsBalanceUserGroupService upsBalanceUserGroupService;

    @Resource
    private UpsBalanceSmsRecordService  upsBalanceSmsRecordService;

    @Resource
    private UpsSmsChannelService  upsSmsChannelService;



    @Override
    public void balanceSmsWarning() {
        List<UpsBalanceSmsConfigEntity> balanceSmsConfigEntityList = upsBalanceSmsWarningDao.findAll();
       for(UpsBalanceSmsConfigEntity entity : balanceSmsConfigEntityList){
           pushSmsWarning(entity);
        }
    }


    private  void  pushSmsWarning(UpsBalanceSmsConfigEntity rdBalanceSmsConfig){
       UpsBalanceEntity  rdBalance = upsBalanceService.getBalanceByMemberId(rdBalanceSmsConfig.getTppMerNo());
        if(rdBalance != null){
            if(rdBalance.getBalance().compareTo(rdBalanceSmsConfig.getThresholdMoney())<0){
                List<UpsBalanceUserGroupEntity> userGroupsList = upsBalanceUserGroupService.getListByCodeAndTppMerNo("balanceSms",rdBalanceSmsConfig.getTppMerNo());
                for(UpsBalanceUserGroupEntity userGroup : userGroupsList){
                    String context =  MessageFormat.format(rdBalanceSmsConfig.getMsgThresholdContext(),rdBalanceSmsConfig.getTppMerNo(),rdBalanceSmsConfig.getThresholdMoney(),rdBalance.getBalance());
                    for(int i = 0;i < rdBalanceSmsConfig.getNumber();i++){
                        try {
                            SmsSendResponse sms = upsSmsChannelService.sendMessage(userGroup.getUserPhone(), context);
                            saveBalanceSms(sms,context,userGroup.getUserPhone(),rdBalanceSmsConfig.getTppMerNo());
                        }catch (Exception e){
                            logger.error("错误余额发送",e);
                            UpsBalanceSmsRecordEntity record = new UpsBalanceSmsRecordEntity();
                            record.setErrorMsg(e.getMessage());
                            upsBalanceSmsRecordService.saveEntity(record);
                        }
                    }}
            }else{
                List<UpsBalanceUserGroupEntity> userGroupsList = upsBalanceUserGroupService.getListByCodeAndTppMerNo("balanceNormalSms",rdBalanceSmsConfig.getTppMerNo());
                for(UpsBalanceUserGroupEntity userGroup : userGroupsList) {
                    String context = MessageFormat.format(rdBalanceSmsConfig.getMsgDefaultContext(),rdBalanceSmsConfig.getTppMerNo(), rdBalance.getBalance());
                    SmsSendResponse sms = upsSmsChannelService.sendMessage(userGroup.getUserPhone(), context);
                    saveBalanceSms(sms, context, userGroup.getUserPhone(),rdBalanceSmsConfig.getTppMerNo());
                }
            }
        }
    }

   public void saveBalanceSms(SmsSendResponse sms, String context, String userPhone,String tppMerNo){
        UpsBalanceSmsRecordEntity record = new UpsBalanceSmsRecordEntity();
        record.setErrorMsg(sms.getErrorMsg());
        record.setMsgId(sms.getMsgId());
        record.setResultCode(sms.getCode());
        record.setMsgContext(context);
        record.setSendAccount(userPhone);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
       record.setTppMerNo(tppMerNo);
       upsBalanceSmsRecordService.saveEntity(record);
    }
}
