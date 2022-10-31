package com.team6.onandthefarmproductservice.repository;

import com.team6.onandthefarmproductservice.entity.ReservedOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservedOrderRepository extends CrudRepository<ReservedOrder,Long> {

    List<ReservedOrder> findByOrderSerialAndIdempoStatus(String orderSerial, Boolean status);

}
