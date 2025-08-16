package com.project.gulimall.ware.domain.vo;

import lombok.Data;

@Data
public class PurchaseItemDoneVo {
    private Long purchaseDetailId;
    private Integer status;
    private String reason;
}
