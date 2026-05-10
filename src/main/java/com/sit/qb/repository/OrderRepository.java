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

	// JOIN FETCH customer + deliveryAgent + orderItems + menuItem to prevent N+1
	// during JSON serialization of the returned list.
	@Query("SELECT DISTINCT o FROM Order o "
			+ "LEFT JOIN FETCH o.customer "
			+ "LEFT JOIN FETCH o.deliveryAgent "
			+ "LEFT JOIN FETCH o.orderItems oi "
			+ "LEFT JOIN FETCH oi.menuItem "
			+ "WHERE o.customer.id = :customerId "
			+ "ORDER BY o.orderDate DESC")
	List<Order> findByCustomerIdOrderByOrderDateDesc(@Param("customerId") Long customerId);

	@Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.menuItem WHERE o.id = :orderId")
	Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

	@Query("SELECT SUM(oi.unitPrice * oi.quantity) FROM OrderItem oi WHERE oi.order.id = :orderId")
	Double calculateTotal(@Param("orderId") Long orderId);

	@Query("SELECT NEW com.sit.qb.dtos.DeliveryReportDto(a.name, c.name, o.id, o.status, o.totalAmount) "
			+ "FROM Order o JOIN o.customer c JOIN o.deliveryAgent a")
	List<DeliveryReportDto> getDeliveryReport();

	// Subquery in WHERE so JOIN FETCH on orderItems returns ALL items per order
	// (not just items belonging to this restaurant). Prevents N+1 on serialization.
	@Query("SELECT DISTINCT o FROM Order o "
			+ "LEFT JOIN FETCH o.customer "
			+ "LEFT JOIN FETCH o.deliveryAgent "
			+ "LEFT JOIN FETCH o.orderItems oi "
			+ "LEFT JOIN FETCH oi.menuItem "
			+ "WHERE o.id IN ("
			+ "  SELECT o2.id FROM Order o2 JOIN o2.orderItems oi2 "
			+ "  WHERE oi2.menuItem.restaurant.id = :restaurantId"
			+ ") "
			+ "ORDER BY o.orderDate DESC")
	List<Order> findByRestaurantId(@Param("restaurantId") Long restaurantId);

}
