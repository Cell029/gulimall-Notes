package com.project.common.to;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemConfirmTo {
    private Long skuId;
    private String title;
    private Boolean check = true;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
}
