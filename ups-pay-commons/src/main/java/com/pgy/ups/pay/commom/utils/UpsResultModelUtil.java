package com.pgy.ups.pay.commom.utils;

import com.pgy.ups.pay.interfaces.enums.UpsResultEnum;
import com.pgy.ups.pay.interfaces.model.UpsResultModel;

import java.io.Serializable;
import java.util.HashMap;

public class UpsResultModelUtil {
	
	private UpsResultModelUtil() {}

    public static UpsResultModel upsResultModelSuccess(){
        return  new UpsResultModel(UpsResultEnum.SUCCESS);
    }

    public static UpsResultModel upsResultModelSuccess(Serializable result){
        return  new UpsResultModel(UpsResultEnum.SUCCESS,result);
    }


    /**
     * 单独使用，懒的写
     * @param code
     * @param message
     * @return
     */
    public static UpsResultModel upsResultModelSuccess(String code,String message,String... strings){
        HashMap<String,String> map = new HashMap<>(16);
        map.put("respCode",strings[0]);
        map.put("respMessage",strings[1]);
        return  new UpsResultModel(code,message,map);

    }
}
