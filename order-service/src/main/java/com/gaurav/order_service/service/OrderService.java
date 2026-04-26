package com.gaurav.order_service.service;

import com.gaurav.order_service.dto.OrderLineItemsDto;
import com.gaurav.order_service.dto.OrderLineItemsResponse;
import com.gaurav.order_service.dto.OrderRequest;
import com.gaurav.order_service.dto.OrderResponse;
import com.gaurav.order_service.model.Order;
import com.gaurav.order_service.model.OrderLineItems;
import com.gaurav.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    @Tool(description = "Place a new order for one or more products")
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        orderRepository.save(order);
    }

    @Tool(description = "Retrieve all placed orders from the system")
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setOrderNumber(order.getOrderNumber());
        orderResponse.setOrderLineItemsList(order.getOrderLineItemsList().stream()
                .map(this::mapToLineItemResponse).toList());
        return orderResponse;
    }

    private OrderLineItemsResponse mapToLineItemResponse(OrderLineItems item) {
        OrderLineItemsResponse response = new OrderLineItemsResponse();
        response.setId(item.getId());
        response.setSkuCode(item.getSkuCode());
        response.setPrice(item.getPrice());
        response.setQuantity(item.getQuantity());
        return response;
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
