package com.github.mitote.store.mongo;

import static com.github.mitote.store.query.QueryBuilder.and;
import static com.github.mitote.store.query.QueryBuilder.eq;
import static com.github.mitote.store.query.QueryBuilder.missing;

import java.util.Collection;

import com.github.mitote.store.MailMessage;
import com.github.mitote.store.MailStore;
import com.github.mitote.store.mongo.convert.DBObjectConverter;
import com.github.mitote.store.mongo.convert.MailMessageDBObject;
import com.github.mitote.store.query.And;
import com.github.mitote.store.query.Query;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


public class MongoDbStore implements MailStore {

	private static final String TAKEN_FIELD = "_taken";
	private static final String DEFAULT_DBNAME = "mitote";
	
	private Mongo mongo;
	private String dbName = DEFAULT_DBNAME;
	private MongoDbQueryBuilder queryBuilder = new MongoDbQueryBuilder();
	private DBObjectConverter dboConverter;
	
	
	public MongoDbStore() {
	}
	
	
	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}
	
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	
	public void put(MailMessage message) {
        getMailCollection().save( new MailMessageDBObject( message, getDb() ) );
	}


	public MailMessage take(Query query) {
		And filter = and( missing(TAKEN_FIELD) /*, before(TAKEN_FIELD, new Date())*/ );
//		DBObject mongoQuery = new BasicDBObject(TAKEN_FIELD, null); 
		if (query.getFilter() != null) {
			filter.add( query.getFilter() );
//			mongoQuery.putAll( query.getFilter().accept( queryBuilder ) );
		}
		DBObject mongoQuery = filter.accept( queryBuilder );
		
		DBObject mongoSort = new BasicDBObject();
		if (query.getOrder() != null) {
			mongoSort.putAll( query.getOrder().accept( queryBuilder ) );
		}
		
		DBObject mongoUpdate = new BasicDBObject( "$set", new BasicDBObject(TAKEN_FIELD, true) );
		DBObject mongoMessage = findAndModify( "mail", mongoQuery, mongoSort, mongoUpdate );
		
		return mongoMessage != null ? getDboConverter().toMailMessage( mongoMessage ) : null;
	}
	

	public void release(MailMessage message) {
		DBObject query = and( eq(TAKEN_FIELD, true), eq("_id", message.getStoreId()) ).accept( queryBuilder );
		DBObject dbo = new MailMessageDBObject( message, getDb() );
		getMailCollection().update( query, dbo );
		
		DBObject unsetTaken = new BasicDBObject( "$unset", new BasicDBObject(TAKEN_FIELD, 1) );
		getMailCollection().update( query, unsetTaken );
	}


	public MailMessage get(Query query) {
		DBObject mongoQuery = query.accept( queryBuilder );
		DBObject message = getMailCollection().findOne( mongoQuery );
		return message != null ? getDboConverter().toMailMessage( message ) : null;
	}


	public Collection<MailMessage> find(Query query) {
		DBObject mongoQuery = query.accept( queryBuilder );
		DBCursor cursor = getMailCollection().find( mongoQuery );
		if (query.getOffset() != null) {
			cursor.skip( query.getOffset() );
		}
		if (query.getLimit() != null) {
			cursor.limit( query.getLimit() );
		}
		return getDboConverter().toMailMessageIterable( cursor );
	}


	public void remove(Query query) {
		DBObject mongoFilter = query.getFilter().accept( queryBuilder );
		getMailCollection().remove( mongoFilter );
	}

	//------------------------------------------------------------------------
	
	protected DBObject findAndModify(String collection, DBObject query, DBObject sort, DBObject update) {
		DBObject cmd = new BasicDBObject();
		cmd.put("findandmodify", collection);
		cmd.put("query", query);
		cmd.put("sort", sort);
		cmd.put("update", update);
		CommandResult result = getDb().command(cmd);
		if (!result.ok() && !result.getErrorMessage().equals("No matching object found")) {
			result.throwOnError();
		}
		return (DBObject) result.get("value");		
	}
	
	
	protected Mongo getMongo() {
		if (mongo == null) {
			try {
				setMongo( new Mongo() );
			}
			catch (Exception e) {
				throw new RuntimeException("Unable to create default mongo connection");
			}
		}
		return mongo;
	}
	
	
	protected DB getDb() {
		return getMongo().getDB( dbName );
	}
	
	
	protected DBCollection getMailCollection() {
		return getDb().getCollection("mail");
	}

	
	protected DBObjectConverter getDboConverter() {
		if (dboConverter == null) {
			dboConverter = new DBObjectConverter( getDb() );			
		}
		return dboConverter;
	}
}
