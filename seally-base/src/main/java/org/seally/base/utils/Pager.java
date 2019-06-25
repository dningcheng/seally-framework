package org.seally.base.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2018年4月14日
 * @author dnc
 * @Description 分页参数包装类
 */
public class Pager<T> {
	
	private Integer curPage = 1;
	private Integer pageSize = 10;
	private Integer total = 0;
	private List<T> dataList = new ArrayList<>();
	
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
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
}
