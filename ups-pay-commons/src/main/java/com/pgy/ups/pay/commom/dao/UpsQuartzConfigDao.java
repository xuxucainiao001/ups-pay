package com.pgy.ups.pay.commom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pgy.ups.pay.interfaces.entity.QuartzConfigEntity;

@Repository
public interface UpsQuartzConfigDao extends JpaRepository<QuartzConfigEntity, Long>{

	QuartzConfigEntity queryByClassName(String className);
    
	@Modifying
	@Query("update QuartzConfigEntity e set e.active=?2 where e.className=?1")
	void setQuartzConfigActive(String className,boolean flag);

}
