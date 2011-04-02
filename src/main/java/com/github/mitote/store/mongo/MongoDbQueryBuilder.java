package com.github.mitote.store.mongo;

import com.github.mitote.store.query.And;
import com.github.mitote.store.query.Criterion;
import com.github.mitote.store.query.Equals;
import com.github.mitote.store.query.Exists;
import com.github.mitote.store.query.In;
import com.github.mitote.store.query.LessThan;
import com.github.mitote.store.query.Order;
import com.github.mitote.store.query.Query;
import com.github.mitote.store.query.QueryVisitor;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class MongoDbQueryBuilder implements QueryVisitor<DBObject> {

	public DBObject visit(Query query) {
		BasicDBObject dbo = new BasicDBObject();
		if (query.getFilter() != null) {
			dbo.put( "$query", query.getFilter().accept(this) );
		}
		if (query.getOrder() != null) {
			dbo.put( "$orderby", query.getOrder().accept(this) );
		}
		return dbo;
	}
	

	public DBObject visit(And and) {
		BasicDBObject o = new BasicDBObject();
		for (Criterion criterion : and) {
			o.putAll( criterion.accept(this) );
		}		
		return o;
	}
	

	public DBObject visit(Equals<?> equals) {
		return new BasicDBObject( equals.getField(), equals.getValue() );
	}


	public DBObject visit(LessThan<?> lt) {
		return new BasicDBObject( lt.getField(), new BasicDBObject("$lt", lt.getValue()) );
	}
	
	
	public DBObject visit(In<?> in) {
		BasicDBList list = new BasicDBList();
		for (Object item : in.getValue()) {
			list.add(item);
		}
		return new BasicDBObject( in.getField(), new BasicDBObject("$in", list) );
	}
	

	public DBObject visit(Exists exists) {
		return new BasicDBObject( exists.getField(), new BasicDBObject("$exists", exists.getValue()) );
	}
	
	
	public DBObject visit(Order order) {
		return new BasicDBObject( order.getField(), order.isAscendant() ? 1 : -1 );
	}

	
	public static DBObject build(Query query) {
		return query.accept( new MongoDbQueryBuilder() );
	}
	
}
