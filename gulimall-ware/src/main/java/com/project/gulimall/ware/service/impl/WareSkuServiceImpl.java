package com.project.gulimall.ware.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.TypeReference;
import com.project.common.to.mq.StockDetailTo;
import com.project.common.to.mq.StockLockedTo;
import com.project.common.utils.R;
import com.project.gulimall.ware.domain.entity.OrderEntity;
import com.project.gulimall.ware.domain.entity.WareOrderTaskDetailEntity;
import com.project.gulimall.ware.domain.entity.WareOrderTaskEntity;
import com.project.gulimall.ware.domain.vo.*;
import com.project.common.exception.NoStockException;
import com.project.gulimall.ware.feign.OrderFeignService;
import com.project.gulimall.ware.feign.ProductFeignService;
import com.project.gulimall.ware.service.StockLockService;
import com.project.gulimall.ware.service.WareOrderTaskDetailService;
import com.project.gulimall.ware.service.WareOrderTaskService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.ware.dao.WareSkuDao;
import com.project.gulimall.ware.domain.entity.WareSkuEntity;
import com.project.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service("wareSkuService")
@RabbitListener(queues = "stock.release.queue")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId) && !"0".equals(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId) && !"0".equals(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 如果没有这个库存记录，那就是新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities == null || wareSkuEntities.isEmpty()) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName(data.get("skuName").toString());
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkusHaveStock(List<Long> skuIds) {
        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            // 查询当前 sku 的总库存量
            // select sum(stock - stock_locked) from wms_ware_sku where sku_id = ?
            Long stock = wareSkuDao.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(stock != null && stock > 0);
            return vo;
        }).collect(Collectors.toList());
        return skuHasStockVos;
    }

    /*@Override
    @Transactional
    public OrderLockResult orderLockStock(WareSkuLockVo wareSkuLockVo) {

        List<StockLockedTo> pendingMessages = new ArrayList<>(); // 收集消息数据
        *//**
         * 保存库存工作单详情
         *//*
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        // 1. 找到每个商品在哪个仓库都有库存
        List<OrderItemVo> lockItems = wareSkuLockVo.getLockItems();
        List<SkuWareHasStock> skuWareHasStocks = lockItems.stream().map(lockItem -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = lockItem.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(lockItem.getCount());
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareIds(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());
        // 2. 锁定库存
        for (SkuWareHasStock skuWareHasStock : skuWareHasStocks) {
            Boolean skuStockLocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareIds();
            if (wareIds.isEmpty()) {
                throw new NoStockException(skuId);
            }

            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, skuWareHasStock.getNum());
                if (count == 1) {
                    // 库存锁定成功
                    skuStockLocked = true;
                    // 保存当前商品的库存操作单详情
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", skuWareHasStock.getNum(), wareOrderTaskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);

                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    pendingMessages.add(stockLockedTo);

                    // 告诉 MQ 库存锁定成功
                    *//*StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    stockLockedTo.setStockDetailTo(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);*//*
                    break;
                } else {
                    // 当前仓库库存锁定失败，尝试下一个仓库
                }
            }
            if (!skuStockLocked) {
                // 当前仓库都库存不足，没法锁住
                throw new NoStockException(skuId);
            }
        }
        // 3. 锁定成功
        return new OrderLockResult(true, pendingMessages);
    }*/

    @Autowired
    private StockLockService stockLockService;

    @Override
    public Boolean orderLockStockWithMessage(WareSkuLockVo wareSkuLockVo) {
        try {
            // 1. 先执行数据库操作（有事务）
            OrderLockResult orderLockResult = stockLockService.orderLockStock(wareSkuLockVo);
            if (orderLockResult.getSuccess()) {
                // 2. 事务提交后再发送消息，需要重新查询数据来构造消息
                List<StockLockedTo> stockLockedTos = orderLockResult.getStockLockedTos();
                if (!stockLockedTos.isEmpty()) {
                    for (StockLockedTo stockLockedTo : stockLockedTos) {
                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);
                    }
                } else {
                    log.error("消息为空: {}", stockLockedTos);
                }
                log.info("消息发送成功，订单号: {}", wareSkuLockVo.getOrderSn());
            }
            return orderLockResult.getSuccess();
        } catch (NoStockException e) {
            log.warn("库存不足，skuId: {}", e.getSkuId());
            return false;
        } catch (Exception e) {
            log.error("库存锁定系统异常", e);
            throw new RuntimeException("库存锁定失败", e);
        }
    }


    @Override
    public void unLockStock(StockLockedTo stockLockedTo) {
        StockDetailTo stockDetailTo = stockLockedTo.getStockDetailTo();
        Long detailId = stockDetailTo.getId();
        // 查询数据库关于此订单的锁定库存信息
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = wareOrderTaskDetailService.getById(detailId);
        if (wareOrderTaskDetailEntity != null) {
            // 如果有此订单的锁定库存信息，就对该订单进行查询，没有该订单才进行解锁
            Long taskId = stockLockedTo.getTaskId();
            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(taskId);
            if (wareOrderTaskEntity != null) {
                String orderSn = wareOrderTaskEntity.getOrderSn(); // 根据订单号查询订单状态
                R r = orderFeignService.getOrderStatus(orderSn);
                if (r.getCode() == 0) {
                    OrderEntity orderEntity = r.getData("orderEntity", new TypeReference<OrderEntity>() {
                    });
                    if (orderEntity == null || orderEntity.getStatus() == 4) {
                        // 订单不存在或者已经被取消，解锁库存
                        if (wareOrderTaskEntity.getTaskStatus() == 1) {
                            // 当前库存工作详情单状态为 1，即库存已锁定才能进行解锁
                            unLockStock(stockDetailTo.getSkuId(), stockDetailTo.getWareId(), stockDetailTo.getSkuNum(), stockDetailTo.getId());
                        }
                    }
                } else {
                    throw new RuntimeException("调用远程服务失败...");
                }
            }

        } else {
            // 库存锁定失败，已经数据回滚，因此无需解锁
        }
    }


    private void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        // 库存解锁
        // wareSkuDao.unLockStock(skuId, wareId, num);
        wareSkuDao.unLockStock(skuId, wareId, num);
        // 更新库存工作单状态
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(taskDetailId);
        wareOrderTaskDetailEntity.setLockStatus(2); // 变为已解锁
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
    }


}