package com.github.mitote.store.query;

import java.util.Date;


public class QueryBuilder {

	public static Query query(Criterion... criteria) {
		if (criteria.length == 1) {
			return new Query( criteria[ 0 ] );
		}
		else {
			return new Query( new And( criteria ) );
		}
	}
	
	
	public static Query query(Order order) {
		return new Query( null, order );
	}
	
	
	public static Query all() {
		return query();
	}
	
	
	public static And and(Criterion... criteria) {
		return new And( criteria );
	}
	
	
	public static <T> Equals<T> eq(String field, T value) {
		return new Equals<T>( field, value );
	}
	
	
	public static <T> LessThan<T> lt(String field, T value) {
		return new LessThan<T>( field, value );
	}
	
	
	public static LessThan<Date> before(String field, Date date) {
		return new LessThan<Date>( field, date );
	}
	
	
	public static <T> In<T> in(String field, T... values) {
		return new In<T>( field, values );
	}
	
	
	public static Exists exists(String field) {
		return new Exists( field, true );
	}
	
	
	public static Exists missing(String field) {
		return new Exists( field, false );
	}
	
	
	public static Order order(String field) {
		return new Order( field, true );
	}
	
	
	public static Order orderDesc(String field) {
		return new Order( field, false );
	}
}
