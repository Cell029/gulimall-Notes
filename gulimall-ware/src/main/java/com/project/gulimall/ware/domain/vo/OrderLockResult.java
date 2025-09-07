package com.project.gulimall.ware.domain.vo;

import com.project.common.to.mq.StockLockedTo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLockResult {
    private Boolean success = false;
    private List<StockLockedTo> stockLockedTos;
}
