package com.project.gulimall.product.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2Vo { // 2 级分类 vo
    private String catalog1Id; // 1 级父分类 id
    private List<Catalog3Vo> catalog3List; // 3 级子分类
    private String id;
    private String name;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3Vo { // 3 级分类 vo
        private String catalog2Id; // 2 级父分类 id
        private String id;
        private String name;
    }
}
