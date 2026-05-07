package com.sit.qb.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sit.qb.dtos.AssignAgentResponseDto;
import com.sit.qb.dtos.Menu_Qty;
import com.sit.qb.dtos.OrderDetailDto;
import com.sit.qb.dtos.OrderItemDto;
import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.dtos.OrderStatusResponseDto;
import com.sit.qb.entity.CustomerProfile;
import com.sit.qb.entity.DeliveryAgentProfile;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Order;
import com.sit.qb.entity.OrderItem;
import com.sit.qb.enums.OrderStatus;
import com.sit.qb.exceptions.ConflictException;
import com.sit.qb.exceptions.IllegalStateTransitionException;
import com.sit.qb.exceptions.ResourceNotFoundException;
import com.sit.qb.repository.CustomerProfileRepository;
import com.sit.qb.repository.DeliveryAgentProfileRepository;
import com.sit.qb.repository.MenuItemRepository;
import com.sit.qb.repository.OrderRepository;

@Service
public class OrderServiceImpl {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private DeliveryAgentProfileRepository agentProfileRepository;

    @Transactional
    public Order placeOrder(OrderRequestDto orderDto) {
        CustomerProfile customer = customerProfileRepository.findById(orderDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (Menu_Qty itemDto : orderDto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + itemDto.getMenuItemId()));

            if (!menuItem.getIsAvailable()) {
                throw new IllegalStateTransitionException("Menu item not available: " + menuItem.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity((int) itemDto.getQuantity());
            orderItem.setUnitPrice(menuItem.getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            totalAmount += menuItem.getPrice() * itemDto.getQuantity();
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    // QB-7: Assign delivery agent
    @Transactional
    public AssignAgentResponseDto assignAgent(Long orderId, Long agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateTransitionException("Cannot assign agent to a completed or cancelled order");
        }

        DeliveryAgentProfile agent = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery agent not found: " + agentId));

        if (!agent.getIsAvailable()) {
            throw new ConflictException("Agent is already assigned to another order");
        }

        order.setDeliveryAgent(agent);
        agent.setIsAvailable(false);

        return new AssignAgentResponseDto(
                "Agent " + agent.getName() + " assigned to Order #" + orderId,
                orderId, agentId, agent.getName());
    }

    // QB-8: Get full order details
    public OrderDetailDto getOrderDetail(Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        List<OrderItemDto> items = order.getOrderItems().stream()
                .map(oi -> new OrderItemDto(oi.getMenuItem().getName(), oi.getQuantity(), oi.getUnitPrice()))
                .collect(Collectors.toList());

        return new OrderDetailDto(
                order.getId(),
                order.getCustomer().getName(),
                order.getStatus(),
                order.getOrderDate(),
                items,
                order.getTotalAmount());
    }

    // QB-10: Get all orders by customer
    public List<Order> getOrdersByCustomer(Long customerId) {
        customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
    }

    // QB-11: Filter orders by status (Specification / Criteria API)
    public List<Order> getOrdersByStatus(String statusStr) {
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateTransitionException("Invalid order status: " + statusStr);
        }
        Specification<Order> spec = (root, query, cb) -> cb.equal(root.get("status"), status);
        return orderRepository.findAll(spec);
    }

    // QB-14: Get total bill for an order
    public Double getOrderTotal(Long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        Double total = orderRepository.calculateTotal(orderId);
        return total != null ? total : 0.0;
    }

    // QB-16: Update order status (lifecycle)
    @Transactional
    public OrderStatusResponseDto updateStatus(Long orderId, String newStatusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateTransitionException("Invalid order status: " + newStatusStr);
        }

        OrderStatus current = order.getStatus();

        if (current == OrderStatus.DELIVERED || current == OrderStatus.CANCELLED) {
            throw new IllegalStateTransitionException("Order is in a terminal state: " + current);
        }

        boolean valid = (current == OrderStatus.PLACED && newStatus == OrderStatus.PREPARING)
                || (current == OrderStatus.PLACED && newStatus == OrderStatus.CANCELLED)
                || (current == OrderStatus.PREPARING && newStatus == OrderStatus.OUT_FOR_DELIVERY)
                || (current == OrderStatus.OUT_FOR_DELIVERY && newStatus == OrderStatus.DELIVERED);

        if (!valid) {
            throw new IllegalStateTransitionException(
                    "Invalid transition from " + current + " to " + newStatus);
        }

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED && order.getDeliveryAgent() != null) {
            order.getDeliveryAgent().setIsAvailable(true);
        }

        return new OrderStatusResponseDto(orderId, current, newStatus, LocalDateTime.now());
    }
}
