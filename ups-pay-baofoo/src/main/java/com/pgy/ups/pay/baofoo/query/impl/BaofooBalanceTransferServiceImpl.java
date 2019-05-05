package com.pgy.ups.pay.baofoo.query.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooRsaCodingUtil;
import com.pgy.ups.pay.baofoo.utils.BaofooSecurityUtil;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceEntity;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceTransferConfigEntity;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceTransferRecordEntity;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceService;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceTransferConfigService;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceTransferRecordService;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceTransferService;
import com.pgy.ups.pay.interfaces.service.config.UpsTppMerConfigService;

@Service(group="baofooBalanceTransferServiceImpl",timeout=60000,retries=0)
public class BaofooBalanceTransferServiceImpl implements UpsBalanceTransferService {


    private Logger logger = LoggerFactory.getLogger(BaofooBalanceTransferServiceImpl.class);


    private static  BigDecimal  maxBalanceMoney = new BigDecimal(2000000);


    @Resource
    private UpsBalanceService upsBalanceService;

   @Resource
   private UpsBalanceTransferRecordService upsBalanceTransferRecordService;

    @Resource
    private UpsBalanceTransferConfigService  upsBalanceTransferConfigService;

    @Resource
    private UpsTppMerConfigService upsTppMerConfigService;


    public void balanceTransfer(UpsBalanceTransferConfigEntity dto ) {
        UpsBalanceEntity rdBalance = upsBalanceService.getBalanceByMemberId(dto.getPayMemberId());
        logger.info("转账{}",dto.getPayMemberId());
        if(rdBalance != null){
            if(rdBalance.getBalance().compareTo(dto.getThresholdMoney()) > 0){
                BigDecimal transMonry =  rdBalance.getBalance().subtract(dto.getThresholdMoney());
                if(transMonry.compareTo(maxBalanceMoney) > 0){
                    transMonry = maxBalanceMoney;
                }
                Map<String,String> map = upsTppMerConfigService.queryUpsTppMerConfig("baofoo", dto.getPayMemberId(),"protocolPay");
                String reslut = null;
                UpsBalanceTransferRecordEntity record = new UpsBalanceTransferRecordEntity();
                record.setMemberId(dto.getPayMemberId());
                record.setToMemberId(dto.getReceiptMemberId());
                record.setCreateTime(new Date());
                record.setTransferMoney(transMonry);
                try {
                    reslut =  payForAnother(map,dto.getReceiptMemberId(),transMonry);
                } catch (Exception e) {
                    record.setReturnCode("-1");
                    record.setErrorReason(e.toString());
                    upsBalanceTransferRecordService.insertSelective(record);
                    logger.error("宝付调用异常",e);
                    throw  new BussinessException("2","宝付调用异常"+e.getMessage());
                }
                logger.info("宝付转账{}",reslut);
                JSONObject returnJsonObject = JSONObject.parseObject(reslut);
                JSONObject  transContent = returnJsonObject.getJSONObject("trans_content");
                JSONObject transHead = transContent.getJSONObject("trans_head");
                String code =  transHead.getString("return_code");
                String returnMsg =  transHead.getString("return_msg");
                record.setReturnCode(code);
                record.setErrorReason(returnMsg);
                if(!code.equals("0000")){
                    upsBalanceTransferRecordService.insertSelective(record);
                    throw  new BussinessException("2","宝付" + returnMsg);
                }
                JSONArray transReqDatas = transContent.getJSONArray("trans_reqDatas");
                JSONObject transReqData  = (JSONObject)transReqDatas.get(0);
                JSONObject  trans = transReqData.getJSONObject("trans_reqData");
                record.setStatus(trans.getString("state"));
                record.setRansBatchid(trans.getString("trans_batchid"));
                record.setTransNo(trans.getString("trans_no"));
                record.setTransOrderid(trans.getString("trans_orderid"));
                upsBalanceTransferRecordService.insertSelective(record);
            }else {
                UpsBalanceTransferRecordEntity record = new UpsBalanceTransferRecordEntity();
                record.setErrorReason("不满足转账条件，转账失败");
                upsBalanceTransferRecordService.insertSelective(record);
            }
        }

    }








    public String payForAnother(Map<String,String> map,String memberId,BigDecimal transMoney) throws IOException {
        Map<String,String> memberIdMap = upsTppMerConfigService.queryUpsTppMerConfig("baofoo", memberId,"protocolPay");

            if(memberIdMap == null){
                logger.error("tpp配置没有查询到{}",map.get("member_id"));
                throw  new BussinessException("2","tpp配置没有查询到");
            }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("trans_no",getTransNo(memberId));//	商户号
        jsonObject.put("trans_money", transMoney);//
        jsonObject.put("to_acc_name", memberIdMap.get("to_acc_name"));
        jsonObject.put("to_acc_no", memberIdMap.get("to_acc_no)"));//
        jsonObject.put("to_member_id",memberId);//商户号
        // jsonObject.put("trans_summary", "222");//

        JSONArray reqDataArray = new JSONArray();
        reqDataArray.add(jsonObject);
        JSONObject transReqData  = new JSONObject();
        transReqData.put("trans_reqData",reqDataArray);
        reqDataArray = new JSONArray();
        reqDataArray.add(transReqData);
        JSONObject transReqDatas  = new JSONObject();
        transReqDatas.put("trans_reqDatas",reqDataArray);
        JSONObject transContents  = new JSONObject();
        transContents.put("trans_content",transReqDatas);
        String origData = transContents.toString();

        String re_Url = "https://public.baofoo.com/baofoo-fopay/pay/BF0040007.do";//正式请求地址
        try {
            origData =  new String(BaofooSecurityUtil.Base64Encode(origData));
        } catch (UnsupportedEncodingException e) {

        }
        String keyStorePath = map.get("pfxpath");
        String keyStorePassword = map.get("pfxpwd");
        String pub_key = map.get("cerpath");
        logger.info("宝付转账真实传参{},{},{}",origData,keyStorePath,keyStorePassword);
        String encryptData = BaofooRsaCodingUtil.encryptByPriPfxFile(origData,
                keyStorePath, keyStorePassword);

        HashMap<String,Object> postParams = new HashMap<>();
        postParams.put("member_id",map.get("member_id"));//	商户号
        postParams.put("terminal_id",map.get("terminal_id"));//	终端号s
        postParams.put("data_type", "json");//	返回报文数据类型xml 或json
        postParams.put("version", "4.0.0");//版本号
        postParams.put("data_content",encryptData);//
        String  reslut;
        logger.info("宝付转账传参{}",postParams.toString());
        String retrunString = OkHttpUtil.postForm(re_Url, postParams);
        logger.info("宝付转账返回参数{}",retrunString);
        if(retrunString.contains("trans_content")) {
            reslut = retrunString;
        } else{
            reslut = BaofooRsaCodingUtil.decryptByPubCerFile(retrunString, pub_key);
            //第二步BASE64解密
            try {
                reslut = BaofooSecurityUtil.Base64Decode(reslut);
            } catch (IOException e) {
            }
        }
        return reslut;
    }

    private String  getTransNo(String member_id){
        LocalDateTime localDateTime3 = LocalDateTime.now();
        String  localDate = localDateTime3.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        Random rand = new Random(System.currentTimeMillis());
        int i = rand.nextInt(999);
        String str = String.valueOf(i);
        String returnStr = member_id + localDate;
        for(int j=0;j < 3-str.length();j++){
            returnStr +="0";
        }
        return returnStr;
    }

    @Override
    public void balanceTransferQuartz() {
        List<UpsBalanceTransferConfigEntity>  balanceTransferList =  upsBalanceTransferConfigService.getList();
        for(UpsBalanceTransferConfigEntity entity :balanceTransferList){
            balanceTransfer(entity );
        }
    }
}
