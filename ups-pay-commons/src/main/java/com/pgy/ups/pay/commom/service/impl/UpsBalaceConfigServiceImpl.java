package com.pgy.ups.pay.commom.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.pgy.ups.common.exception.BussinessException;
import com.pgy.ups.common.utils.OkHttpUtil;
import com.pgy.ups.pay.commom.dao.UpsBalaceConfigDao;
import com.pgy.ups.pay.commom.utils.SecurityUtils;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceConfigEntity;
import com.pgy.ups.pay.interfaces.entity.UpsBalanceEntity;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalaceConfigService;
import com.pgy.ups.pay.interfaces.service.balance.UpsBalanceService;
import com.pgy.ups.pay.interfaces.service.config.UpsTppMerConfigService;


@Service
public class UpsBalaceConfigServiceImpl implements UpsBalaceConfigService {

    @Resource
    private UpsBalaceConfigDao upsBalaceConfigDao;

    @Resource
    private UpsTppMerConfigService upsTppMerConfigService;

    @Resource
    private UpsBalanceService balanceService;

    private Logger logger = LoggerFactory.getLogger(UpsBalaceConfigServiceImpl.class);


    @Override
    public void balaceConfigeQuartzTask() {
        List<UpsBalanceConfigEntity> upsBalanceConfigEntityList = upsBalaceConfigDao.findAll();

        for(UpsBalanceConfigEntity entity : upsBalanceConfigEntityList){
            queryThirdpartyBalace(entity.getPayChannel(),entity.getTppMerNo(),"queryBalance");
        }

    }

    @Async
    private void queryThirdpartyBalace(String payChannel,String tppMerNo,String orderType){
        Map<String,Object> postParaPms = new HashMap<>();
        Map<String,String> map =  upsTppMerConfigService.queryUpsTppMerConfig(payChannel,tppMerNo,orderType);
        if(map == null || map.size()==0){
            logger.error("余额配置没有查询到{}",tppMerNo);
            return;
        }
        postParaPms.put("member_id",  map.get("member_id"));//	商户号
        postParaPms.put("terminal_id", map.get("terminal_id"));//终端号
        postParaPms.put("return_type", "json");//	返回报文数据类型xml 或json
        postParaPms.put("trans_code", "BF0001");//	交易码
        postParaPms.put("version", "4.0");//版本号
        postParaPms.put("account_type", "1");//帐户类型--0:全部、1:基本账户、2:未结算账户、3:冻结账户、4:保证金账户、5:资金托管账户；
        String MAK = "&";//分隔符
        String KeyString = map.get("tpp_privateKey");
        String Md5AddString = "member_id="+postParaPms.get("member_id")+MAK+"terminal_id="+postParaPms.get("terminal_id")+MAK+"return_type="+postParaPms.get("return_type")+MAK+"trans_code="+postParaPms.get("trans_code")+MAK+"version="+postParaPms.get("version")+MAK+"account_type="+postParaPms.get("account_type")+MAK+"key="+KeyString;
        //log("Md5拼接字串:"+Md5AddString);//商户在正式环境不要输出此项以免泄漏密钥，只在测试时输出以检查验签失败问题
        String Md5Sing = null;//必须为大写
        try {
            Md5Sing = SecurityUtils.md5(Md5AddString).toUpperCase();
        } catch (Exception e) {
            throw  new BussinessException("3","调用错误Md5Sing" + e.getMessage());
        }
        postParaPms.put("sign", Md5Sing);
        String Re_Url = "https://public.baofoo.com/open-service/query/service.do";//正式请求地址
        String retrunString = null;
        try {
            logger.info("余额查询传参{}",postParaPms.toString());
            retrunString = OkHttpUtil.postForm(Re_Url, postParaPms);
            logger.info("余额返回参数{}",retrunString);
        } catch (IOException e) {
            logger.error("余额查询异常",e);
            throw  new BussinessException("1","调用错误");
        }
        JSONObject jsonObject = JSONObject.parseObject(retrunString);
        JSONObject  transContent = jsonObject.getJSONObject("trans_content");
        JSONObject transHead = transContent.getJSONObject("trans_head");
        String code =  transHead.getString("return_code");
        String returnMsg =  transHead.getString("return_msg");
        if(!"0000".equals(code)){
            throw  new BussinessException("2","调用错误" + returnMsg);
        }
        JSONObject transReqDatas = transContent.getJSONObject("trans_reqDatas");
        JSONObject transReqData =  transReqDatas.getJSONObject("trans_reqData");
        BigDecimal balance  = transReqData.getBigDecimal("balance");
        UpsBalanceEntity balanceDto =  balanceService.getBalanceByMemberId(tppMerNo);
        if(balanceDto == null){
            balanceDto = new UpsBalanceEntity();
            balanceDto.setTppMerNo(tppMerNo);
            balanceDto.setBalance(balance);
            balanceDto.setCreateTime(new Date());
            balanceDto.setUpdateTime(new Date());
            balanceService.saveEntity(balanceDto);
        }else{
            balanceDto.setBalance(balance);
            balanceDto.setUpdateTime(new Date());
            balanceService.updateEntity(balanceDto);
        }
    }
}
