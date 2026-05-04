package com.sit.qb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sit.qb.dtos.DeliveryReportDto;
import com.sit.qb.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

	List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);

	@Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.menuItem WHERE o.id = :orderId")
	Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

	@Query("SELECT SUM(oi.unitPrice * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
	Double calculateTotal(@Param("orderId") Long orderId);

	@Query("SELECT NEW com.sit.qb.dtos.DeliveryReportDto(a.name, c.name, o.id, o.status, o.totalAmount) "
			+ "FROM Order o JOIN o.customer c JOIN o.deliveryAgent a")
	List<DeliveryReportDto> getDeliveryReport();

}
