package com.github.mitote.store.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class And extends Criterion implements Iterable<Criterion> {

	private List<Criterion> criteria = new ArrayList<Criterion>();

	
	public And(Criterion... criteria) {
		this.criteria.addAll( Arrays.asList(criteria) );
	}
	
	
	public Iterator<Criterion> iterator() {
		return Collections.unmodifiableList( criteria ).iterator();
	}
	
	
	public void add(Criterion criterion) {
		criteria.add( criterion );
	}
	
	
	@Override
	public <R> R accept(QueryVisitor<R> visitor) {
		return visitor.visit( this );
	}

}
