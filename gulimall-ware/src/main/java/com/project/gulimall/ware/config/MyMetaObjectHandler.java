package com.project.gulimall.ware.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    // 插入时自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间和更新时间
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }

    // 更新时自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        // 只更新更新时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
