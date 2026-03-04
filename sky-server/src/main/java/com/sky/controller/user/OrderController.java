package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.alibaba.fastjson.JSON;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.websocket.WebSocketServer;
import com.sky.vo.OrderVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "用户订单相关接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private WebSocketServer webSocketServer;

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单，参数为：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO=orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 历史订单查询
     *
     * @param page
     * @param pageSize
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        PageResult pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<?> cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<?> repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }
    @GetMapping("/reminder/{id}")
    @ApiOperation("客户催单")
    public Result reminder(@PathVariable Long id){
        orderService.reminder(id);
        return Result.success();
    }



    /**
     * 模拟支付成功（不调用真实微信支付API）
     * 更新订单状态为已支付、待接单，返回成功让前端直接跳转支付成功页面
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("模拟支付成功")
    public Result<?> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("模拟支付成功，订单号：{}", ordersPaymentDTO.getOrderNumber());
        
        // 更新订单状态为已支付、待接单（这样历史订单才能看到）
        orderService.payment(ordersPaymentDTO);

        // 通过WebSocket向前端推送来单提醒
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1); // 1表示新订单提醒
        map.put("orderNumber", ordersPaymentDTO.getOrderNumber());
        map.put("content", "有新订单，订单号：" + ordersPaymentDTO.getOrderNumber());

        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
        
        // 返回简单成功，不返回支付参数，让前端直接跳转支付成功页面
        return Result.success();
    }
}
