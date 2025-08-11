package com.project.gulimall.product;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.gulimall.product.entity.BrandEntity;
import com.project.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

	@Autowired
	private BrandService brandService;
	@Autowired
	private OSSClient ossClient;

	@Test
	void contextLoads() {
		// 新增
		/*BrandEntity brandEntity = new BrandEntity();
		brandEntity.setName("华为");
		brandService.save(brandEntity);
		System.out.println("保存成功...");*/

		// 修改
		/*BrandEntity brandEntity = new BrandEntity();
		brandEntity.setBrandId(1L);
		brandEntity.setDescript("华为高端机");
		brandService.updateById(brandEntity);
		System.out.println("修改成功...");*/

		// 查询
		List<BrandEntity> brandEntityList = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
		System.out.println("查询成功：");
		brandEntityList.forEach(System.out::println);
	}

	@Test
	void testUpload() throws FileNotFoundException {
		InputStream inputStream = new FileInputStream("F:\\pictures\\100.jpg");
		ossClient.putObject("cell-gmall", "101.jpg", inputStream);
		ossClient.shutdown();
		System.out.println("上传成功！");
	}

}
