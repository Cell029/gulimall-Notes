package com.project.gulimall.ware.domain.vo;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseDoneVo {
    @NotNull
    private Long purchaseId;

    private List<PurchaseItemDoneVo> items;

}
