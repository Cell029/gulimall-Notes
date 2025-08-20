package com.project.gulimall.product.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.gulimall.product.domain.vo.Catalog2Vo;
import com.project.gulimall.product.service.CategoryBrandRelationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;
import com.project.gulimall.product.dao.CategoryDao;
import com.project.gulimall.product.domain.entity.CategoryEntity;
import com.project.gulimall.product.service.CategoryService;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao; // 等价于 baseMapper
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1. 查出所有分类
        List<CategoryEntity> entities = categoryDao.selectList(null);
        // 2. 组装成父子的树形结构
        // 找出所有的一级分类，即该分类没有父类，也就是父类的 id 为 0
        List<CategoryEntity> level1Menu = entities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                // 根据这个父类找它的子菜单
                .map(menu -> {
                    // 调用该方法递归地找到所有子菜单
                    menu.setChildren(getChildren(menu, entities));
                    return menu;
                })
                .sorted(Comparator.comparingInt(menu -> menu.getSort() == null ? 0 : menu.getSort()))
                .collect(Collectors.toList());

        return level1Menu;
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        // TODO 1. 检擦当前删除的菜单是否被别的地方引用
        categoryDao.delete(new LambdaQueryWrapper<CategoryEntity>().in(CategoryEntity::getCatId, list));
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        // 如果有父分类，就向上找
        // 因为添加进 path 的 id 是从最小层级开始的，所以 path 中的 id 排序为 三级 -> 二级 -> 一级
        // 所以进行一下反转操作
        Collections.reverse(findParentPath(catelogId, paths));
        return paths.toArray(new Long[0]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return categoryDao.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, 0));
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            // 缓存未命中，查询数据库
            System.out.println("缓存未命中...准备查询数据库...");
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDb();
            // 存入缓存
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 转换成 JSON
                String jsonStr = objectMapper.writeValueAsString(catalogJsonFromDb);
                // 存入 Redis 的是标准 JSON
                stringRedisTemplate.opsForValue().set("catalogJson", jsonStr);
            } catch (JsonProcessingException e) {
                log.error("JSON序列化失败：{}", e.getMessage());
            }
            // 直接返回数据库查询结果，避免重复解析
            return catalogJsonFromDb;
        }
        System.out.println("getCatalogJson 缓存命中...");
        log.info("catalogJson: {}", catalogJson);
        // 将获取到的缓存数据转换成需要的对象类型
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Catalog2Vo>> result = null;
        try {
            result = objectMapper.readValue(
                    catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
                    }
            );
        } catch (Exception e) {
            log.error("JSON解析失败：{}", e.getMessage());
            // 清除错误缓存
            stringRedisTemplate.delete("catalogJson");
        }
        return result;
    }

    private List<CategoryEntity> getCategoryParent(List<CategoryEntity> categoryEntities, Long parentCid) {
        // 查询那些 parent_cid = 当前分类 id 的数据
        List<CategoryEntity> collect = categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return collect;
    }

    /**
     * 从数据库查询并封装三级分类数据
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {

        synchronized (this) {
            // 得到锁后应该再去缓存中查询一遍，如果没有再进行查询数据库
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            if (!StringUtils.isEmpty(catalogJson)) {
                System.out.println("getCatalogJsonFromDb 缓存命中...");
                // 将获取到的缓存数据转换成需要的对象类型
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<Catalog2Vo>> result = null;
                try {
                    result = objectMapper.readValue(
                            catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
                            }
                    );
                } catch (Exception e) {
                    log.error("JSON解析失败：{}", e.getMessage());
                    // 清除错误缓存
                    stringRedisTemplate.delete("catalogJson");
                }
                return result;
            }
            System.out.println("开始查询数据库...");
            List<CategoryEntity> categoryEntities = categoryDao.selectList(null);

            // 1. 查出所有一级分类
            // List<CategoryEntity> level1Categories = getLevel1Categories();
            List<CategoryEntity> level1Categories = getCategoryParent(categoryEntities, 0L);
            // 2. 封装数据
            Map<String, List<Catalog2Vo>> collect = level1Categories.stream().collect(
                    Collectors.toMap(level1Category -> {
                        return level1Category.getCatId().toString();
                    }, level1Category -> {
                        // 查询此一级分类的所有二级分类
                        List<CategoryEntity> category2Entities = getCategoryParent(categoryEntities, level1Category.getCatId());
                        List<Catalog2Vo> catelog2Vos = null;
                        if (!category2Entities.isEmpty()) {
                            catelog2Vos = category2Entities.stream().map(category2Entity -> {
                                Catalog2Vo catelog2Vo = new Catalog2Vo(
                                        level1Category.getCatId().toString(),
                                        null,
                                        category2Entity.getCatId().toString(),
                                        category2Entity.getName()
                                );
                                // 找三级分类
                                List<CategoryEntity> category3Entities = getCategoryParent(categoryEntities, category2Entity.getCatId());
                                if (!category3Entities.isEmpty()) {
                                    List<Catalog2Vo.Catalog3Vo> catelog3Vos = category3Entities.stream().map(category3Entity -> {
                                        Catalog2Vo.Catalog3Vo catelog3Vo = new Catalog2Vo.Catalog3Vo(
                                                category2Entity.getCatId().toString(),
                                                category3Entity.getCatId().toString(),
                                                category3Entity.getName()
                                        );
                                        return catelog3Vo;
                                    }).collect(Collectors.toList());
                                    catelog2Vo.setCatalog3List(catelog3Vos);
                                }
                                return catelog2Vo;
                            }).collect(Collectors.toList());
                        }
                        if (catelog2Vos != null && !catelog2Vos.isEmpty()) {
                            return catelog2Vos;
                        } else {
                            return Collections.emptyList();
                        }
                    }));
            // 存入缓存
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 转换成 JSON
                String jsonStr = objectMapper.writeValueAsString(collect);
                // 存入 Redis 的是标准 JSON
                stringRedisTemplate.opsForValue().set("catalogJson", jsonStr);
            } catch (JsonProcessingException e) {
                log.error("JSON序列化失败：{}", e.getMessage());
            }
            return collect;
        }
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 收集当前节点 id
        paths.add(catelogId);
        // 根据 catelogId 查出当前分类的信息
        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0) {
            findParentPath(category.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 获取子菜单
     *
     * @param root 当前菜单
     * @param all  所有菜单
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
                // 找到子菜单
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                // 对子菜单进行排序
                .sorted(Comparator.comparingInt(categoryEntity -> categoryEntity.getSort() == null ? 0 : categoryEntity.getSort()))
                .collect(Collectors.toList());
        return children;
    }


}