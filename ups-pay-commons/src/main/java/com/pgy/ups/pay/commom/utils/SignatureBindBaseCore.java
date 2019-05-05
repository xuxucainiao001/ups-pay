package com.pgy.ups.pay.commom.utils;

import com.pgy.data.handler.domain.PgyData;
import com.pgy.data.handler.service.PgyDataHandlerService;
import com.pgy.data.handler.service.impl.PgyDataHandlerServiceImpl;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.pay.interfaces.entity.EncryptSignModel;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.entity.UpsUserSignLogEntity;
import com.pgy.ups.pay.interfaces.enums.SignTypeEnum;
import com.pgy.ups.pay.interfaces.model.UpsParamModel;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignLogService;
import com.pgy.ups.pay.interfaces.service.auth.UpsAuthSignService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class SignatureBindBaseCore {
    @Resource
    private UpsAuthSignService upsAuthSignService;

    @Resource
    private UpsAuthSignLogService upsAuthSignLogService;



    public UpsAuthSignEntity checkBaofooSignatureBind(UpsParamModel orderEntity, SignTypeEnum signTypeEnum) {
        PgyDataHandlerService pgyDataHandlerService = new PgyDataHandlerServiceImpl();
        UpsAuthSignEntity upsAuthSignBaofooEntity = new UpsAuthSignEntity();
        upsAuthSignBaofooEntity.setMerchantCode(orderEntity.getMerchantCode());
        upsAuthSignBaofooEntity.setPayChannel(orderEntity.getPayChannel());
        upsAuthSignBaofooEntity.setUserNo(orderEntity.getUserNo());
        upsAuthSignBaofooEntity.setBankMd5(pgyDataHandlerService.md5(orderEntity.getBankCard()));
        upsAuthSignBaofooEntity.setSignType(signTypeEnum.getCode());
        upsAuthSignBaofooEntity.setRealNameMd5(pgyDataHandlerService.md5(orderEntity.getRealName()));
        upsAuthSignBaofooEntity.setPhoneNoMd5(pgyDataHandlerService.md5(orderEntity.getPhoneNo()));
        upsAuthSignBaofooEntity.setIdentityMd5(pgyDataHandlerService.md5(orderEntity.getIdentity()));
        upsAuthSignBaofooEntity.setBankCode(orderEntity.getBankCode());
        UpsAuthSignEntity upsAuthSignEntity = upsAuthSignService.queryUpsAuthSignBaofoo(upsAuthSignBaofooEntity);
        if (upsAuthSignEntity != null && 10 == upsAuthSignEntity.getStatus()) {
            throw new BussinessException("2000", "已经完成过签约");
        }
        return upsAuthSignEntity;
    }


    protected UpsAuthSignEntity creatBaofooSignatureBind(UpsParamModel orderEntity) {
        UpsAuthSignEntity upsAuthSignEntity = new UpsAuthSignEntity();
        userEncrypt(orderEntity,upsAuthSignEntity);
        upsAuthSignEntity.setId(IdGenerateWorker.getInstance().nextId());
        upsAuthSignEntity.setPayChannel(orderEntity.getPayChannel());
        upsAuthSignEntity.setUserNo(orderEntity.getUserNo());
        upsAuthSignEntity.setBankCode(orderEntity.getBankCode());
        upsAuthSignEntity.setMerchantCode(orderEntity.getMerchantCode());
        upsAuthSignEntity.setBusinessFlowNum(orderEntity.getBusinessFlowNum());
        upsAuthSignEntity.setBusinessType(orderEntity.getBusinessType());
        return upsAuthSignEntity;
    }

    protected static HashMap<String, Object> requestMap(String txnSubType, Map<String, String> map) {
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("data_type", "json");
        requestMap.put("version", "4.0.0.0");
        requestMap.put("txn_sub_type", txnSubType);
        requestMap.put("txn_type", "0431");
        requestMap.put("member_id", map.get("member_id"));
        requestMap.put("terminal_id", map.get("terminal_id"));
        return requestMap;
    }

    protected String getUpsOrderNo(UpsParamModel upsParamModel) {
        return upsParamModel.getPayChannel() + upsParamModel.getMerchantCode() + IdGenerateWorker.getInstance().nextId();
    }

    protected String getUpsUserId(UpsParamModel upsParamModel) {
        return MD5Utils.MD5Encode(upsParamModel.getPayChannel() + upsParamModel.getMerchantCode() + upsParamModel.getUserNo() + upsParamModel.getPhoneNo() + upsParamModel.getBankCard(), "UTF-8");
    }


    /**
     * @param upsParamModel
     * @param upsUserId
     * @param tppMerNo
     * @param tppOrderNo
     * @param json
     * @return
     */
    protected UpsUserSignLogEntity saveTppRequestParams(UpsParamModel upsParamModel, String upsUserId, String tppMerNo, String tppOrderNo, String json) {
        UpsUserSignLogEntity upsUserSignLogEntity = new UpsUserSignLogEntity();
        userEncrypt(upsParamModel,upsUserSignLogEntity);
        upsUserSignLogEntity.setId(IdGenerateWorker.getInstance().nextId());
        upsUserSignLogEntity.setMerchantCode(upsParamModel.getMerchantCode());
        upsUserSignLogEntity.setOrderType(upsParamModel.getOrderType());
        upsUserSignLogEntity.setBusinessFlowNum(upsParamModel.getBusinessFlowNum());
        upsUserSignLogEntity.setUserNo(upsParamModel.getUserNo());
        upsUserSignLogEntity.setUpsUserId(upsUserId);
        upsUserSignLogEntity.setTppMerNo(tppMerNo);
        upsUserSignLogEntity.setTppOrderNo(tppOrderNo);
        upsUserSignLogEntity.setTppResponseTime(new Date());
        upsUserSignLogEntity.setTppRequestParams(json);
        upsAuthSignLogService.saveRecord(upsUserSignLogEntity);
        return upsUserSignLogEntity;
    }

    protected UpsUserSignLogEntity saveTppResponseParams(UpsUserSignLogEntity upsUserSignLogEntity, String json) {
        upsUserSignLogEntity.setTppResponseParams(json);
        upsUserSignLogEntity.setTppResponseTime(new Date());
        upsAuthSignLogService.saveRecord(upsUserSignLogEntity);
        return upsUserSignLogEntity;
    }


    private void userEncrypt(UpsParamModel upsParamModel, EncryptSignModel encryptSignModel){
        PgyDataHandlerService pgyDataHandlerService = new PgyDataHandlerServiceImpl();
        PgyData realNamPayDate = pgyDataHandlerService.chineseName(upsParamModel.getRealName());
        PgyData phoneNoPayDate = pgyDataHandlerService.mobilePhone(upsParamModel.getPhoneNo());
        PgyData identityPayDate = pgyDataHandlerService.idcard(upsParamModel.getIdentity());
        PgyData bankCardPayDate = pgyDataHandlerService.bankCard(upsParamModel.getBankCard());
        encryptSignModel.setBankEncrypt(bankCardPayDate.getValEncrypt());
        encryptSignModel.setBankMd5(bankCardPayDate.getValMd5());
        encryptSignModel.setRealNameEncrypt(realNamPayDate.getValEncrypt());
        encryptSignModel.setRealNameMd5(realNamPayDate.getValMd5());
        encryptSignModel.setIdentityMd5(identityPayDate.getValMd5());
        encryptSignModel.setIdentityEncrypt(identityPayDate.getValEncrypt());
        encryptSignModel.setPhoneNoEncrypt(phoneNoPayDate.getValEncrypt());
        encryptSignModel.setPhoneNoMd5(phoneNoPayDate.getValMd5());
        encryptSignModel.setBankCard(bankCardPayDate.getValMask());
        encryptSignModel.setRealName(realNamPayDate.getValMask());
        encryptSignModel.setIdentity(identityPayDate.getValMask());
        encryptSignModel.setPhoneNo(phoneNoPayDate.getValMask());
    }



}