package org.seally.base.common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description 
 * @Date 2019年1月14日
 * @author 邓宁城
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ResponseBody
	@ExceptionHandler(ApiException.class)
	public ApiResponse<String> defaultExceptionHandler(HttpServletRequest req,ApiException e){

		return new ApiResponse<String>(e.getCode(),e.getDesc());
	}
}
