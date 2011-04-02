package com.github.mitote.store.query;


public class Equals<T> extends FieldComparison<T> {

	public Equals(String field, T value) {
		super(field, value);
	}

	
	@Override
	public <R> R accept(QueryVisitor<R> visitor) {
		return visitor.visit(this);
	}

}
