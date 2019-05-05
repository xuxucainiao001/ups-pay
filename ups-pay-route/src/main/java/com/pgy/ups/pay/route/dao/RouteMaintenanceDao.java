package com.pgy.ups.pay.route.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.RouteMaintenanceEntity;

@Repository
public interface RouteMaintenanceDao extends JpaRepository<RouteMaintenanceEntity, Long>{
    
	@Query("from RouteMaintenanceEntity r where r.active=true and (now() between r.startTime and r.endTime)")
	List<RouteMaintenanceEntity> queryAllRouteMaintenance();

}
