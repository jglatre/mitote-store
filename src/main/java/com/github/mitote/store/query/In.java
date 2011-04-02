package com.github.mitote.store.query;

import java.util.Arrays;


public class In<T> extends FieldComparison<Iterable<T>> {
	
	public In(String name, T... value)  {
		super(name, Arrays.asList(value));
	}

	
	public In(String name, Iterable<T> value) {
		super(name, value);
	}


	@Override
	public <R> R accept(QueryVisitor<R> visitor) {
		return visitor.visit(this);
	}

}
