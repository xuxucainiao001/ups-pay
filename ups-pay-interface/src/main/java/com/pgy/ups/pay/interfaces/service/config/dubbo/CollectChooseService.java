package com.pgy.ups.pay.interfaces.service.config.dubbo;

import com.pgy.ups.common.page.PageInfo;
import com.pgy.ups.pay.interfaces.entity.CollectChooseEntity;
import com.pgy.ups.pay.interfaces.form.CollectChooseForm;

public interface CollectChooseService {

	PageInfo<CollectChooseEntity> queryByForm(CollectChooseForm form);

	void deleteCollectChooseById(Long id);

	boolean updateCollectChoose(CollectChooseForm form);

	CollectChooseEntity createCollectChoose(CollectChooseForm form);

}
