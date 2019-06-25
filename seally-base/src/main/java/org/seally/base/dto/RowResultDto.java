package org.seally.base.dto;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;

/**
 * @Date 2018年10月27日
 * @author dnc
 * @Description 单行数据操作结果vo
 */
public class RowResultDto {
	
	public static final boolean OPT_SUCCESS = true;
	public static final boolean OPT_FAILED = false;
	
	/**
	 * 主键值（根据实际业务需要指定）
	 */
	private String id;
	/**
	 * 操作结果
	 */
	private Boolean result = OPT_SUCCESS;
	/**
	 * 附加返回数据（根据实际业务需要指定）
	 */
	private Object data;
	
	public RowResultDto(){}
	
	public RowResultDto(String id){
		this.id = id;
	}
	
	public RowResultDto(Boolean result){
		this.result = result;
	}
	
	public RowResultDto(String id,Boolean result){
		this(result);
		this.id = id;
	}
	
	public RowResultDto(String id,Object data){
		this(id);
		this.data = data;
	}
	
	public RowResultDto(String id,Boolean result,Object data){
		this(id,result);
		this.data = data;
	}
	
	public static RowResultDto success(){
		return new RowResultDto();
	}
	
	public static RowResultDto success(String id){
		return new RowResultDto(id);
	}
	
	public static RowResultDto success(String id,Object data){
		return new RowResultDto(id,data);
	}
	
	
	public static RowResultDto error(){
		return new RowResultDto(OPT_FAILED);
	}
	
	public static RowResultDto error(String id){
		return new RowResultDto(id,OPT_FAILED);
	}
	
	public static RowResultDto error(String id,Object data){
		return new RowResultDto(id,OPT_FAILED,data);
	}
	
	public String getId() {
		return id;
	}
	public void setBiz(String id) {
		this.id = id;
	}
	public Boolean getResult() {
		return result;
	}
	public void setResult(Boolean result) {
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
