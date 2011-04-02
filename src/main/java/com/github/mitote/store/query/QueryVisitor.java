package com.github.mitote.store.query;


public interface QueryVisitor<R> {

	R visit(Query query);
	R visit(And and);
	R visit(Equals<?> equals);
	R visit(LessThan<?> lt);
	R visit(In<?> in);
	R visit(Exists exists);
	R visit(Order order);
}
