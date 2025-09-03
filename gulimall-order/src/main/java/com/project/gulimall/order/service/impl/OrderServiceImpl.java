package com.project.gulimall.order.service.impl;

import com.project.common.domain.vo.MemberResponseVo;
import com.project.common.to.CartItemConfirmTo;
import com.project.gulimall.order.feign.CartFeignService;
import com.project.gulimall.order.feign.MemberFeignService;
import com.project.gulimall.order.interceptor.LoginUserInterceptor;
import com.project.gulimall.order.vo.MemberAddressVo;
import com.project.gulimall.order.vo.OrderConfirmVo;
import com.project.gulimall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.order.dao.OrderDao;
import com.project.gulimall.order.entity.OrderEntity;
import com.project.gulimall.order.service.OrderService;

@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private CartFeignService cartFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询订单确认页需要用到的数据
     */
    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberResponseVo memberResponseVo = LoginUserInterceptor.threadLocal.get();
        // 1. 远程查询当前用户的所有收获地址
        List<MemberAddressVo> address = memberFeignService.getAddressByUserId(memberResponseVo.getId());
        // 2. 远程查询购物项信息
        List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems(memberResponseVo.getId());
        // 3. 查询用户积分
        Integer integration = memberResponseVo.getIntegration();

        orderConfirmVo.setAddress(address);
        orderConfirmVo.setItems(items);
        orderConfirmVo.setIntegration(integration);
        // 4. 总价在实体类中自动计算，这里无需赋值

        // 5. TODO 防重令牌的添加

        return orderConfirmVo;
    }

}