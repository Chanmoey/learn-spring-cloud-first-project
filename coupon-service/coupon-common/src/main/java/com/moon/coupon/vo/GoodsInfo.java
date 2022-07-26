package com.moon.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * fake 商品信息
 * @author Chanmoey
 * @date 2022年07月27日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfo {

    /**
     * 商品类型
     */
    private Integer type;

    private Double price;

    private Integer count;
}
