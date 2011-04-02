package com.github.mitote.store.query;

public class LessThan<T> extends FieldComparison<T> {
	
	protected LessThan(String field, T value) {
		super(field, value);
	}

	
	@Override
	public <R> R accept(QueryVisitor<R> visitor) {
		return visitor.visit( this );
	}

}
