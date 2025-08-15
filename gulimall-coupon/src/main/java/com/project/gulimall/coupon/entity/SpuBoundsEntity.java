package com.project.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品spu积分设置
 */
@Data
@TableName("sms_spu_bounds")
public class SpuBoundsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private Long spuId;
	/**
	 * 成长积分
	 */
	private BigDecimal growBounds;
	/**
	 * 购物积分
	 */
	private BigDecimal buyBounds;
	/**
	 * 优惠生效情况[1111（四个状态位，从右到左）;
	 * 右边第一位 - 无优惠，0 成长积分不赠送，1 成长积分赠送;
	 * 右边第二位 - 无优惠，0 购物积分不赠送，1 购物积分赠送;
	 * 右边第三位 - 有优惠，0 成长积分不赠送，1 成长积分赠送;
	 * 右边第四位 - 有优惠，0 购物积分不赠送，1 购物积分赠送
	 */
	private Integer work;

}
