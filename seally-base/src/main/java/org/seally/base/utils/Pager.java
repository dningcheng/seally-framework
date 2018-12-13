package org.seally.base.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * @Date 2018年4月14日
 * @author dnc
 * @Description 分页类
 */
public class Pager<T extends Pager<?>> {
	
	private Integer curPage = 1;
	private Integer pageSize = 10;
	private Integer total = 0;
	private List<? extends Pager<?>> dataList = new ArrayList<>();
	
	public Integer getCurIndex() {
		if(null == getCurPage()) return null;
		return (getCurPage()-1)*pageSize;
	}
	public Integer getTopPage() {
		if(null == getCurPage()) return null;
		return 1;
	}
	public Integer getPrePage() {
		if(null == getCurPage()) return null;
		return getCurPage()-1>1?getCurPage()-1:1;
	}
	public Integer getCurPage() {
		return curPage;
	}
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
	public Integer getNextPage() {
		if(null == getCurPage() || null == getBottomPage()) return null;
		return getCurPage()+1<getBottomPage()?getCurPage()+1:getBottomPage();
	}
	public Integer getBottomPage() {
		if(null == getTotal() || null == getPageSize()) return null;
		if(getTotal() == 0){
			return getTopPage();
		}
		return getTotal()%getPageSize() == 0?getTotal()/getPageSize():(getTotal()/getPageSize()+1);
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public List<? extends Pager<?>> getDataList() {
		return dataList;
	}
	public void setDataList(List<? extends Pager<?>> dataList) {
		this.dataList = dataList;
	}
	
	/**
	 * @Date 2018年10月27日
	 * @author dnc
	 * @Description 清理元素的分页属性
	 * 为了继承自该分页类的其他pojo类需要清理这些不必要的分页属性时调用（一般在返回客户端之前这些属性时不需要的需要清理掉）
	 * @return PagerUtil<?>
	 */
	public Pager<?> clearPager(){
		this.curPage = null;this.pageSize=null;this.total=null;this.dataList=null;return this;
	}
	
	/**
	 * @Date 2018年10月27日
	 * @author dnc
	 * @Description 清理list中所有元素的分页属性
	 * 为了方便接收分页参数，一般需要分页的实体都自称自该分页类，
	 * 但是如此在查询到列表返回时每个元素便无意中被增加了此分页类的属性，
	 * 因此建议在必要的时候返回之前调用该方法进行清理再返回
	 * @param targetList
	 */
	public static List<? extends Pager<?>> clearAllPager(List<? extends Pager<?>> targetList){
		if(null == targetList)	return null;
		targetList.forEach(target -> target.clearPager());
		return targetList;
	}
	
	/**
	 * @Date 2018年10月27日
	 * @author dnc
	 * @Description 获取当前分页对象的JSONString字符串
	 * @return String
	 */
	public String getPagerJSON() {
		if(null == getCurPage()) return null;
		
		Map<String,Object> pagerMap = new HashMap<>();
		pagerMap.put("curIndex", getCurIndex());
		pagerMap.put("topPage", getTopPage());
		pagerMap.put("prePage", getPrePage());
		pagerMap.put("curPage", getCurPage());
		pagerMap.put("nextPage", getNextPage());
		pagerMap.put("bottomPage", getBottomPage());
		pagerMap.put("pageSize", getPageSize());
		pagerMap.put("total", getTotal());
		
		return JSON.toJSONString(pagerMap);
	}
	
	
}
