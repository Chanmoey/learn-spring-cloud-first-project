package com.moon.coupon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 模板系统测试程序
 *
 * @author Chanmoey
 * @date 2022年07月26日
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateApplicationTests {

    @Test
    public void contextLoad() {
        System.out.println("环境没问题");
    }
}
