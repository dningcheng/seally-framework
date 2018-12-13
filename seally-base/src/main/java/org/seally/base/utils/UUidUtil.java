package org.seally.base.utils;

import java.util.UUID;

/**
 * @Date 2018年08月02日
 * @author dnc
 * @Description
 */
public class UUidUtil {
	public static String generateUUid() {
		// 生成唯一uuid
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
}
