package com.mt4agents.paginate;

public class PaginationSettings {
	private Integer offset;
	private Integer rowcount;
	private String search;
	private Integer sortColumn;
	private String sortDirection;
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getRowcount() {
		return rowcount;
	}
	public void setRowcount(Integer rowcount) {
		this.rowcount = rowcount;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public Integer getSortColumn() {
		return sortColumn;
	}
	public void setSortColumn(Integer sortColumn) {
		this.sortColumn = sortColumn;
	}
	public String getSortDirection() {
		return sortDirection;
	}
	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}
}
