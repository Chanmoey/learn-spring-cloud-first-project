package com.moon.coupon.advice;

import com.moon.coupon.exception.CouponException;
import com.moon.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author Chanmoey
 * @date 2022年07月24日
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    public CommonResponse<String> handlerException(HttpServletRequest req, Exception ex) {
        ex.printStackTrace();
        CommonResponse<String> response = new CommonResponse<>(-500, "service error");
        response.setData(ex.getMessage());
        return response;
    }

    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest req, CouponException ex) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setData(ex.getMessage());
        return response;
    }
}
