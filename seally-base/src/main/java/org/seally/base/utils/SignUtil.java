package org.seally.base.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;

/**
 * @Date 2018年10月11日
 * @author dnc
 * @Description 签名工具类
 */
public class SignUtil {
	
	/**
	 * @Date 2018年10月11日
	 * @author dnc
	 * @Description 自己实现的升序排序JSON对象key的方法，会递归排序嵌套JSON对象的key
	 * @param param
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object sortedKey(Object param){
		if(null == param)	return param;
		
		if(param instanceof Map){
			Map<String, Object> paramMap = new TreeMap<>((Map)param);
			for (Entry<String, Object> entry : paramMap.entrySet()) {
				entry.setValue(sortedKey(entry.getValue()));
			}
			param = paramMap;
		}else if(param instanceof List){
			List<Object> list = (List)param;
			for(int i=0; i< list.size();i++){
				list.set(i, sortedKey(list.get(i)));
			}
		}
		return param;
	}
	
	
	/**
	 * @Date 2018年10月11日
	 * @author dnc
	 * @Description
	 * @param jsonObject 签名参数对象格式 ，sign属性会自动排除
	 * @param signSecret 签名密匙
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String sign(Object jsonObject,String signSecret){
		return sign(null == jsonObject ? null : JSON.parseObject(JSON.toJSONString(jsonObject), TreeMap.class), signSecret);
	}
	
	/**
	 * @Date 2018年10月11日
	 * @author dnc
	 * @Description
	 * @param jsonString 签名参数JSON字符串格式 ，sign属性会自动排除
	 * @param signSecret 签名密匙
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String sign(String jsonString,String signSecret){
		return sign(null == jsonString ? null : JSON.parseObject(jsonString, Map.class), signSecret);
	}
	
	
	/**
	 * @Date 2018年10月11日
	 * @author dnc
	 * @Description
	 * @param params 签名参数 ，sign属性会自动排除
	 * @param signSecret 签名密匙
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String sign(Map<String, Object> params,String signSecret){
		
		if(null == params)	return null;
		
		Map<String, Object> sortedParams = new TreeMap<>((Map)sortedKey(params));//排序
		
		StringBuilder buf = new StringBuilder(sortedParams.size() * 20);
		for (Entry<String, Object> entry : sortedParams.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if ("sign".equals(key) || null == value) {
				continue; // 不在此处添加签名，sign参数必须放最后
			}
			buf.append(encodeURL(key))
			   .append('=')
			   .append(JSON.toJSONString(entry.getValue()))
			   .append('&');
		}
		buf.append("key=").append(signSecret);
		
		return EncryptUtil.Encrypt(buf.toString(), EncryptUtil.ENC_MD5);
		
	}
	
	public static String encodeURL(Object value) {
		if (value == null) return null;
		try {
			return URLEncoder.encode(value.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}


