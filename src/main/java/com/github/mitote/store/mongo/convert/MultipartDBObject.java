package com.github.mitote.store.mongo.convert;

import java.util.AbstractList;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import com.mongodb.DB;
import com.mongodb.DBObject;


public class MultipartDBObject extends AbstractList<DBObject> {

	private final Multipart multipart;
	private final DB db;
	
	
	public MultipartDBObject(Multipart multipart, DB db) {
		this.multipart = multipart;
		this.db = db;
	}

	
	@Override
	public DBObject get(int index) {
		try {
			return new PartDBObject<BodyPart>( multipart.getBodyPart(index), db );
		}
		catch (Exception e) {
			throw new RuntimeException();
		}
	}

	
	@Override
	public int size() {
		try {
			return multipart.getCount();
		} 
		catch (MessagingException e) {
			throw new RuntimeException();
		}
	}		
}
