package com.project.common.to.mq;

import lombok.Data;

import java.util.List;

@Data
public class StockLockedTo {
    private Long taskId; // 库存工作单 id
    private StockDetailTo stockDetailTo; // 库存工作详情单
}
