package com.github.mitote.store.query;


public class Query {

	private Criterion filter;
	private Order order;
	private Integer offset;
	private Integer limit;
	
	
	public Query(Criterion filter) {
		this(filter, null);
	}
	
	
	public Query(Criterion filter, Order order) {
		this.filter = filter;
		this.order = order;
	}

	
	public Query(Criterion filter, Order order, int offset, int limit) {
		this.filter = filter;
		this.order = order;
		this.offset = offset;
		this.limit = limit;
	}
	
	
	public Criterion getFilter() {
		return filter;
	}
	
	
	public Order getOrder() {
		return order;
	}
	
	
	public Integer getOffset() {
		return offset;
	}
	
	
	public Integer getLimit() {
		return limit;
	}
	
	
	public <R> R accept(QueryVisitor<R> visitor) {
		return visitor.visit(this);
	}
}
