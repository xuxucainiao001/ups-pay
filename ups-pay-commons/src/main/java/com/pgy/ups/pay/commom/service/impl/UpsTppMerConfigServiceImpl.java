package com.pgy.ups.pay.commom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.pgy.ups.pay.commom.dao.UpsTppMerConfigDao;
import com.pgy.ups.pay.commom.utils.CacheUtils;
import com.pgy.ups.pay.interfaces.cache.Cacheable;
import com.pgy.ups.pay.interfaces.entity.UpsTppMerConfigEntity;
import com.pgy.ups.pay.interfaces.service.config.UpsTppMerConfigService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class UpsTppMerConfigServiceImpl implements UpsTppMerConfigService, Cacheable<UpsTppMerConfigEntity> {

    @Resource
    private UpsTppMerConfigDao upsTppMerConfigDao;

    @Resource
    private CacheUtils cacheUtils;



    @Override
    public String getCacheKeyname() {
        return "ups_tpp_mer_config-cache";
    }

    @Override
    public Map<String, UpsTppMerConfigEntity> getCacheableData() {
        List<UpsTppMerConfigEntity> list =     upsTppMerConfigDao.findAll();
        Map<String, UpsTppMerConfigEntity>  map = new HashMap<>();
        for(UpsTppMerConfigEntity entity :  list){
            map.put(entity.getPayChannel() + entity.getOrderType() + entity.getTppMerNo() , entity);
        }
        return map;
    }

    @Override
    public UpsTppMerConfigEntity queryUpsTppMer(String payChannel, String tppMerNo, String orderType) {
        UpsTppMerConfigEntity entity = cacheUtils.getCacheByRediskeynameAndKey(getCacheKeyname(), payChannel +orderType + tppMerNo,
                UpsTppMerConfigEntity.class);
        if(entity == null){
            UpsTppMerConfigEntity upsTppMerConfigEntity = new UpsTppMerConfigEntity();
            upsTppMerConfigEntity.setOrderType(orderType);
            upsTppMerConfigEntity.setPayChannel(payChannel);
            upsTppMerConfigEntity.setTppMerNo(tppMerNo);
            Optional<UpsTppMerConfigEntity>  optional =   upsTppMerConfigDao.findOne(Example.of(upsTppMerConfigEntity));
            entity  = optional.isPresent() ? optional.get() : null;
            if(entity != null){
                cacheUtils.setCacheByRediskeynameAndKey(getCacheKeyname(), payChannel +orderType + tppMerNo,entity);
            }
        }
        return entity;
    }

    @Override
    public Map<String,String> queryUpsTppMerConfig(String payChannel, String upsTppMer, String orderType) {
        UpsTppMerConfigEntity entity =  queryUpsTppMer(payChannel,upsTppMer,orderType);
        if(entity == null){
            return  null;
        }
        String configDate = entity.getConfigDate();
        Map<String, String> map = JSONObject.parseObject(configDate, new TypeReference<Map<String, String>>() {
        });
        return map;
    }
}
