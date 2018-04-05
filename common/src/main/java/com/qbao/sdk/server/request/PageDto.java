package com.qbao.sdk.server.request;

import java.util.List;

/**
 * 分页VO
 * 
 * @author xuefeihu
 *
 */
public class PageDto<T> {

	/** 当前页 */
	private Integer pageNo = 1;

	/** 页面大小 */
	private Integer pageSize = 1;

	/** 记录总数 */
	private Long totalCount = 0L;

	/** 列表 */
	private List<T> list;
	
	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "PageDto [pageNo=" + pageNo + ", pageSize=" + pageSize + ", totalCount=" + totalCount + ", list=" + list
				+ "]";
	}

}
