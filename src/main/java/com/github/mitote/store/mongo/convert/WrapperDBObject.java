package com.github.mitote.store.mongo.convert;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;


public abstract class WrapperDBObject<T> implements DBObject {

	private final Map<String, Getter<T, ?>> fields = new LinkedHashMap<String, Getter<T, ?>>();
	private final T target;
	private Object id;
//	private DBObject dbo = new BasicDBObject();
	
	private DB db;
	private GridFS gridFS;

	
	public interface Getter<T, V> {
		V get(T target) throws Exception;
	}

	
	protected WrapperDBObject(T target, DB db) {
		this.target = target;	
		this.db = db;

		configureFields(fields);
	}
	
	
	protected void configureFields(Map<String, Getter<T, ?>> fields) {
	}

	
	protected T getTarget() {
		return target;
	}
	
	
	protected DB getDb() {
		return db;
	}
	

	protected GridFS getGridFS() {
		if (gridFS == null) {
			gridFS = new GridFS( db );
		}
		return gridFS;
	}


	public boolean containsField(String field) {
		return fields.containsKey(field);
	}


	public boolean containsKey(String key) {
		return containsField(key);
	}
	

	public Object get(String field) {
		if ("_id".equals(field)) {
			return id;
		}
		
		Getter<T, ?> getter = fields.get(field);
		if (getter != null) {
			try {
				return getter.get(target);
			} 
			catch (Exception e) {
				throw new RuntimeException("Unable to get value from field " + field, e);
			}
		}
		return null;
	}

	
	public boolean isPartialObject() {
		return false;
	}

	
	public Set<String> keySet() {
		return fields.keySet();
	}

	
	public void markAsPartialObject() {
		throw new UnsupportedOperationException();
	}

	
	public Object put(String field, Object value) {
		if ("_id".equals(field)) {
			this.id = value;
			return this.id;
		}
		throw new RuntimeException("Read only field: " + field);
	}


	public void putAll(BSONObject o) {
		throw new UnsupportedOperationException();
	}

	
	public void putAll(Map map) {
		throw new UnsupportedOperationException();
	}

	
	public Object removeField(String field) {
		throw new UnsupportedOperationException();
	}

	
	public Map toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String key : keySet()) {
			map.put(key, get(key));
		}
		return map;
	}

}
