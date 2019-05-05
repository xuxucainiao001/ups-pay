package com.pgy.ups.pay.service.dao;



import com.pgy.ups.pay.interfaces.entity.UpsOrderEntity;
import com.pgy.ups.pay.interfaces.form.UpsOrderForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UpsOrderDubboDao extends JpaRepository<UpsOrderEntity, Long> {

    @Query(value = "SELECT * FROM ups_t_order WHERE merchant_code = :#{#form.merchantCode} AND IF (:#{#form.orderType} !='',order_type = :#{#form.orderType} ,1=1) AND IF (:#{#form.bankMd5} !='',bank_md5 = :#{#form.bankMd5} ,1=1) AND IF (:#{#form.upsOrderCode} !='',ups_order_code = :#{#form.upsOrderCode} ,1=1)  AND IF (:#{#form.businessFlowNum} !='',business_flow_num = :#{#form.businessFlowNum} ,1=1) AND IF (:#{#form.id} !='',id = :#{#form.id} ,1=1) AND IF (:#{#form.userNo} !='',user_no = :#{#form.userNo} ,1=1)  AND IF (:#{#form.orderStatus} !='',order_status = :#{#form.orderStatus} ,1=1) AND IF (:#{#form.payChannel} !='',pay_channel = :#{#form.payChannel} ,1=1) ORDER BY create_time desc",nativeQuery = true)
    Page<UpsOrderEntity> getPage(@Param("form") UpsOrderForm form, Pageable pageable);

}
//     