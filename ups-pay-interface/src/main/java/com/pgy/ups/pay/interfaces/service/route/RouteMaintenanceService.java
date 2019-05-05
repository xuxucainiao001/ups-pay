package com.pgy.ups.pay.interfaces.service.route;

import java.util.List;

import com.pgy.ups.pay.interfaces.entity.RouteMaintenanceEntity;

/**
  *  渠道 银行维护业务
 * @author 墨凉
 *
 */
public interface RouteMaintenanceService {
    
	/**
	  *  查询所有的银行维护列表
	 * @return
	 */
	List<RouteMaintenanceEntity> queryAllRouteMaintenance();

}
