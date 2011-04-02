package com.github.mitote.store.query;


public abstract class Criterion {

	public abstract <R> R accept(QueryVisitor<R> visitor);
}
