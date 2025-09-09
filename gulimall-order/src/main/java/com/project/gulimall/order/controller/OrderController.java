package com.project.gulimall.order.controller;

import java.util.Arrays;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.project.gulimall.order.domain.entity.OrderEntity;
import com.project.gulimall.order.service.OrderService;
import com.project.common.utils.PageUtils;
import com.project.common.utils.R;



/**
 * 订单
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/status/{orderSn}")
    public R getOrderStatus(@PathVariable String orderSn){
        OrderEntity orderEntity = orderService.getByOrderSn(orderSn);
        if (orderEntity == null) {
            return R.error("订单不存在");
        }
        return R.ok().setData("orderEntity", orderEntity);
    }

    /**
     * 查询当前登录用户的所有订单信息
     * @param params
     * @return
     */
    @GetMapping("/listWithItem")
    public R listWithItem(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPageWithItem(params);
        return R.ok().put("page", page);
    }

}
