package com.project.gulimall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.gulimall.product.entity.BrandEntity;
import com.project.gulimall.product.service.BrandService;
import com.project.common.utils.PageUtils;
import com.project.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Valid @RequestBody BrandEntity brand/*, BindingResult bindingResult*/){
        /*if(bindingResult.hasErrors()){
            Map<String, String> map = new HashMap<>();
            // 获取校验的错误结果
            bindingResult.getFieldErrors().forEach(fieldError -> {
                // 获取到错误提示
                String message = fieldError.getDefaultMessage();
                // 获取错误的字段
                String field = fieldError.getField();
                map.put(field, message);
            });
            return R.error(400, "提交的数据不合法").put("data", map);
        } else {
            brandService.save(brand);
            return R.ok();
        }*/

        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody BrandEntity brand){
		brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
