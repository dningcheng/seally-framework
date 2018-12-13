package org.seally.base.utils;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Date 2018年10月10日
 * @author dnc
 * @Description jedis工具类
 */
public class JedisUtil {
	
	public final static String SUCCESS_RESULT = "OK";//redis 操作成功返回值
	
	/**
	 * @Date 2018年10月10日
	 * @author dnc
	 * @Description 判断指定key是否存在缓存中
	 * @param pool
	 * @param key
	 * @return
	 */
	public static boolean exists(JedisPool pool, String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.exists(key);
		}
	}
	
	/**
	 * @Date 2018年10月10日
	 * @author dnc
	 * @Description 获取键名为key的值
	 * @param pool
	 * @param key
	 * @return
	 */
	public static String get(JedisPool pool, String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.get(key);
		}
	}
	
	/**
	 * @Date 2018年10月10日
	 * @author dnc
	 * @Description 删除指定key的缓存
	 * @param pool
	 * @param key
	 * @return
	 */
	public static long del(JedisPool pool, String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.del(key);
		}
	}
	
	/**
	 * @Date 2018年10月10日
	 * @author dnc
	 * @Description 设置value到缓存
	 * @param pool
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public static boolean setex(JedisPool pool, String key, int seconds, Object value) {
		
		return setex(pool, key, seconds, getString(value));
	}
	
	/**
	 * @Date 2018年10月10日
	 * @author dnc
	 * @Description 设置value到缓存
	 * @param pool
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public static boolean setex(JedisPool pool, String key, int seconds, String value) {
		try (Jedis jedis = pool.getResource()) {
			return SUCCESS_RESULT.equalsIgnoreCase(jedis.setex(key, seconds, value));
		}
	}
	
	
	/**
	 * @Date 2018年10月10日
	 * @author dnc
	 * @Description key不存在且设置成功返回true，否则返回false
	 * @param pool
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	public static boolean setnex(JedisPool pool, String key, int seconds, Object value) {
		String json = getString(value);
		try (Jedis jedis = pool.getResource()) {
			if (!jedis.exists(key)) return SUCCESS_RESULT.equalsIgnoreCase(jedis.setex(key, seconds, json));
		}
		return false;
	}
	
	private static String getString(Object value){
		if(value instanceof String ) {
			return value.toString();
		}else {
			return JSON.toJSONString(value);
		}
	}
}
