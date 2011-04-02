package com.github.mitote.store.query;


public class Order {

	private String field;
	private boolean ascendant;
	private Order next;
	
	
	public Order(String field) {
		this(field, true);
	}
	
	
	public Order(String field, boolean ascendant) {
		this(field, ascendant, null);
	}
	
	
	public Order(String field, Order next) {
		this(field, true, next);
	}
	
	
	public Order(String field, boolean ascendant, Order next) {
		this.field = field;
		this.ascendant = ascendant;
		this.next = next;		
	}
	
	
	public String getField() {
		return field;
	}
	
	
	public boolean isAscendant() {
		return ascendant;
	}
	
	
	public Order getNext() {
		return next;
	}
	
	
	public <R> R accept(QueryVisitor<R> visitor) {
		return visitor.visit(this);
	}

}
