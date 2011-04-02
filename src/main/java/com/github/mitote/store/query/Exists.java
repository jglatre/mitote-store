package com.github.mitote.store.query;


public class Exists extends FieldComparison<Boolean> {

	protected Exists(String field, boolean value) {
		super(field, value);
	}

	
	@Override
	public <R> R accept(QueryVisitor<R> visitor) {
		return visitor.visit( this );
	}
}
