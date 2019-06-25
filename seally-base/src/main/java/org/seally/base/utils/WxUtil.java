package org.seally.base.utils;

import java.util.Arrays;

/**
 * @Description 微信相关工具类
 * @Date 2019年4月20日
 * @author 邓宁城
 */
public class WxUtil {
	
	public static String wxToken = "seallyserver";
	
	public static boolean chechSignature(String signature,String timestamp,String nonce) {
		String[] chechSignatureParams= new String[]{wxToken,timestamp,nonce};
		Arrays.sort(chechSignatureParams);
		String chechSignature = EncryptUtil.Encrypt(chechSignatureParams[0]+chechSignatureParams[1]+chechSignatureParams[2],EncryptUtil.ENC_SHA1);
		System.out.println(chechSignature);
		return signature.equals(chechSignature);
		
	}
}
