package org.seally.base.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2018年11月24日
 * @author dnc
 * @Description 模型工具类
 */
public class ModelUtil {
	/**
	 * @Date 2018年8月23日
	 * @author dnc
	 * @param <I> 输入类型
	 * @param <O> 输出类型
	 * @param source 源数据
	 * @Description 将源数据List装在到目标数据List,注意该方式是以目标类targetClass提供原类型作为参数的构造函数完成
	 * @return
	 */
	public static <I, O> List<O> trans2Model(List<I> source,Class<O> targetClass){
		if(null == source || source.isEmpty())	return new ArrayList<>();
		
		List<O> result = new ArrayList<>();
		
		source.forEach(mongo -> {
				try {
					result.add(targetClass.getConstructor(source.get(0).getClass()).newInstance(mongo));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					
					e.printStackTrace();
				}
			}
		);
		
		return result;
	}
}
