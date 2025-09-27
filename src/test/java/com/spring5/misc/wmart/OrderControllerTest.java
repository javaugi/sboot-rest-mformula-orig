/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.wmart;

import com.spring5.dbisolation.wmart.WmOrderController;
import com.spring5.dbisolation.wmart.OrderDto;
import com.spring5.dbisolation.wmart.CreateOrderRequest;
import com.spring5.dbisolation.wmart.WmOrderService;
import java.math.BigDecimal;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WmOrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    WmOrderService orderService;

    @Test
    public void createOrder_returnsOk() throws Exception {
        CreateOrderRequest req = new CreateOrderRequest("o1", "store1", new BigDecimal("10.0"));
        OrderDto dto = new OrderDto("o1", "store1", "CREATED");
        when(orderService.createOrder(eq("idem-1"), any())).thenReturn(dto);

        mockMvc.perform(post("/orders")
            .header("Idempotency-Key", "idem-1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"orderId\":\"o1\",\"storeId\":\"store1\",\"amount\":10.0}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value("o1"));
    }
}
