package com.project.gulimall.ware.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    // 采购单 id
    private Long purchaseId;
    // 采购需求 id 集合
    private List<Long> items;
}
