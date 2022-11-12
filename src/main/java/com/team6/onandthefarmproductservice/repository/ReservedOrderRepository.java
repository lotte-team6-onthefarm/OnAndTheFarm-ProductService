package com.team6.onandthefarmproductservice.repository;

import com.team6.onandthefarmproductservice.entity.ReservedOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ReservedOrderRepository extends CrudRepository<ReservedOrder,Long> {

    List<ReservedOrder> findByOrderSerialAndIdempoStatus(String orderSerial, Boolean status);

    boolean existsByReservedOrderIdAndIdempoStatus(Long id, String status);

    Optional<ReservedOrder> findByOrderSerial(String orderSerial);
}
