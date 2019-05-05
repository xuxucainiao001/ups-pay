package com.pgy.ups.pay.route.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.pgy.ups.pay.interfaces.entity.RouteMaintenanceEntity;
import com.pgy.ups.pay.interfaces.service.route.RouteMaintenanceService;
import com.pgy.ups.pay.route.dao.RouteMaintenanceDao;

/**
 * 渠道 银行维护业务
 * 
 * @author 墨凉
 *
 */
@Service
public class RouteMaintenanceServiceImpl implements RouteMaintenanceService {
    
	@Resource
	private RouteMaintenanceDao routeMaintenanceDao;

	@Override
	public List<RouteMaintenanceEntity> queryAllRouteMaintenance() {

		List<RouteMaintenanceEntity> list = routeMaintenanceDao.queryAllRouteMaintenance();
		return list;
	}

}
