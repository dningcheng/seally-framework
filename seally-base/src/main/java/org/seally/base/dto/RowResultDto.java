package org.seally.base.dto;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;

/**
 * @Date 2018年10月27日
 * @author dnc
 * @Description TODO 单行数据操作结果vo
 */
public class RowResultDto {
	
	public static final Integer NUMBER_OPT_SUCCESS = 1;
	public static final Integer NUMBER_OPT_FAILED = 0;
	
	/**
	 * 主键值（根据实际业务需要指定）
	 */
	private String biz;
	/**
	 * 操作结果值  0=失败   1=成功（默认）
	 */
	private Integer result = NUMBER_OPT_SUCCESS;
	/**
	 * 附加返回数据（根据实际业务需要指定）
	 */
	private Object data;
	
	public RowResultDto(){}
	
	public RowResultDto(String biz){
		this.biz = biz;
	}
	
	public RowResultDto(Integer result){
		this.result = result;
	}
	
	public RowResultDto(String biz,Integer result){
		this(result);
		this.biz = biz;
	}
	
	public RowResultDto(String biz,Object data){
		this(biz);
		this.data = data;
	}
	
	public RowResultDto(String biz,Integer result,Object data){
		this(biz,result);
		this.data = data;
	}
	
	public static RowResultDto success(){
		return new RowResultDto();
	}
	
	public static RowResultDto success(String biz){
		return new RowResultDto(biz);
	}
	
	public static RowResultDto success(String biz,Object data){
		return new RowResultDto(biz,data);
	}
	
	
	public static RowResultDto error(){
		return new RowResultDto(NUMBER_OPT_FAILED);
	}
	
	public static RowResultDto error(String biz){
		return new RowResultDto(biz,NUMBER_OPT_FAILED);
	}
	
	public static RowResultDto error(String biz,Object data){
		return new RowResultDto(biz,NUMBER_OPT_FAILED,data);
	}
	
	public String getBiz() {
		return biz;
	}
	public void setBiz(String biz) {
		this.biz = biz;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
	public static void main(String[] args) {
		System.out.println(JSON.toJSONString(RowResultDto.success()));
		System.out.println(JSON.toJSONString(RowResultDto.success("1000")));
		System.out.println(JSON.toJSONString(RowResultDto.success("1000","操作错误")));
		System.out.println(JSON.toJSONString(RowResultDto.success("1000",new HashMap<>())));
		
		System.out.println(JSON.toJSONString(RowResultDto.error()));
		System.out.println(JSON.toJSONString(RowResultDto.error("1000")));
		System.out.println(JSON.toJSONString(RowResultDto.error("1000","操作错误")));
		System.out.println(JSON.toJSONString(RowResultDto.error("1000",new HashMap<>())));
	}
}
