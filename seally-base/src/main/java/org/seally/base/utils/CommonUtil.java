package org.seally.base.utils;


import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description 没有特定分类的公共工具类
 * @Date 2018年12月20日
 * @author 邓宁城
 */
public class CommonUtil {
	
	
	
	/**
	 * @Description 获取uuid（已去除"-"中划线）
	 * @Date 2018年12月20日
	 * @author 邓宁城
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
    
	/**
	 * @Description 处理url前缀（如果原url没有前缀则拼接传递的前缀，如果已经有前缀则不拼接直接返回）
	 * @Date 2018年5月28日
	 * @author dnc
	 * @param url 原url
	 * @param prefix 前缀
	 * @return
	 */
	public static String handUrlDomain(String url,String domain){
		if(StringUtils.isEmpty(domain) || StringUtils.isAllEmpty(url)){
			return url;
		}
		if(Pattern.matches(".*http.{0,1}://.*", url)){
			return url;
		}
		return domain+url;
	}
	
	/**
	 * @Date 2018年5月5日
	 * @author dnc
	 * @Description 去除多余换行符
	 * @param source
	 * @return
	 */
	public static String removeMultLineBlanks(String source) {  
		 if (source != null) {  
		     Pattern p = Pattern.compile("(\r?\n(\\s*\r?\n)+)");  
		     Matcher m = p.matcher(source);  
		     return m.replaceAll("\r\n");  
		 }  
		 return source;  
   }
	
	/**
	 * @Date 2018年5月23日
	 * @author dnc
	 * @Description 计算比率，传递任何一个参数为null或是0则结果返回0，最大返回1
	 * @param num1 分子
	 * @param num2 分母
	 * @return 返回保留指定小数位（不够补0）的字符串形式浮点数的%占比数如：num1=1 num2=3 points=2 返回33.33
	 */
	public static String calcRatio(Integer num1,Integer num2,Integer points){
		Double result = new Double(0);
		if(num1 == null || num2 == null || num2.intValue() == 0 || num1.intValue() == 0){
			result = 0D;
		}else if(num1.intValue() == num2.intValue()){
			result = 100D;
		}else{
			result = (1.0*num1)/num2 * 100;
		}
		String str = formatDouble(result,points).toString();
		if(points == null || points.intValue() == 0){//保留0位取整
			return str.substring(0, str.indexOf("."));
		}else{
			int len = points-str.substring(str.indexOf(".")).length();
			for(int i=0;i<= len;i++){
				str+="0";
			}
			return str;
		}
	}
	
	/**
	 * @Description 四舍五入保留指定位数，传递为null默认为0
	 * @Date 2018年5月23日
	 * @author 邓宁城
	 * @param source
	 * @param points
	 * @return
	 */
	public static Double formatDouble(Double source,Integer points){
		if (points == null){
			points = 0;
		}
		if (source == null) {
			return new BigDecimal(0).setScale(points, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return new BigDecimal(source).setScale(points, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * @Description 生成系统用户加密密码
	 * @Date 2018年12月20日
	 * @author 邓宁城
	 * @param account 用户注册账号（非空）
	 * @param password 用户注册明文密码（非空）
	 * @return
	 */
	public static String generalPassword(String account,String password){
		
		String encrypt = EncryptUtil.Encrypt(account, EncryptUtil.ENC_MD5);
		
		return EncryptUtil.Encrypt(String.format("%s%s%s", encrypt.substring(0, 16),password,encrypt.substring(16)));
	}
	
}
