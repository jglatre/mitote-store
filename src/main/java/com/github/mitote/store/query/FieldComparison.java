package com.github.mitote.store.query;


public abstract class FieldComparison<T> extends Criterion {

	private String field;
	private T value;
	
	
	protected FieldComparison(String field, T value) {
		this.field = field;
		this.value = value;
	}
	
	
	public String getField() {
		return field;
	}
	
	
	public T getValue() {
		return value;
	}
}
