package com.project.gulimall.product;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.gulimall.product.domain.entity.BrandEntity;
import com.project.gulimall.product.service.BrandService;
import com.project.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

	@Autowired
	private BrandService brandService;
	@Autowired
	private OSSClient ossClient;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedissonClient redissonClient;

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

	@Test
	void testFindParentPath() {
		Long[] catelogPath = categoryService.findCatelogPath(251L);
		log.info("完整路径：{}", Arrays.asList(catelogPath));
	}

	@Test
	void testStringRedisTemplate() {
		stringRedisTemplate.opsForValue().set("hello", "world_" + UUID.randomUUID().toString());
		// 查询
		String hello = stringRedisTemplate.opsForValue().get("hello");
		System.out.println(hello);
	}

	@Test
	void testRedissonClient() {
		System.out.println(redissonClient);
	}

}
