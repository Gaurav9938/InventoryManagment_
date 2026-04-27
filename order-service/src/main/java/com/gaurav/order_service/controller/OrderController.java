package com.gaurav.order_service.controller;

import com.gaurav.order_service.dto.OrderRequest;
import com.gaurav.order_service.dto.OrderResponse;
import com.gaurav.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            orderService.placeOrder(orderRequest);
            return "Order Placed Successfully";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }
}
