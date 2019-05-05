package com.pgy.ups.pay.interfaces.service.auth.dubbo;

import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.UpsAuthSignEntity;
import com.pgy.ups.pay.interfaces.form.UpsUserSignForm;

public interface UpsUserSignService{

    PageInfo<UpsAuthSignEntity> queryByForm(UpsUserSignForm upsBankForm);
}
