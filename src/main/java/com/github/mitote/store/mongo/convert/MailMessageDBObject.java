package com.github.mitote.store.mongo.convert;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.mitote.store.MailMessage;
import com.mongodb.DB;
import com.mongodb.DBObject;


public class MailMessageDBObject extends WrapperDBObject<MailMessage> {

	private static final Map<String, Getter<MailMessage, ?>> FIELDS = new LinkedHashMap<String, Getter<MailMessage,?>>();
	static {
//		FIELDS.put("_id", new Getter<MailMessage, Object>() {
//			public Object get(MailMessage target) {
//				return target.getStoreId();
//			}			
//		} );
		FIELDS.put( "timestamp", new Getter<MailMessage, Date>() {
			public Date get(MailMessage target) {
				return target.getTimestamp();
			}			
		} );
		FIELDS.put( "helo", new Getter<MailMessage, String>() {
			public String get(MailMessage target) {
				return target.getHeloHost();
			}			
		} );
		FIELDS.put( "sender", new Getter<MailMessage, String>() {
			public String get(MailMessage target) {
				return target.getSender();
			}			
		} );
		FIELDS.put( "recipients", new Getter<MailMessage, List<String>>() {
			public List<String> get(MailMessage target) {
				return target.getRecipients();
			}			
		} );
	}
		

	public MailMessageDBObject(MailMessage message, DB db) {
		super( message, db );
	}
	

	@Override
	protected void configureFields(Map<String, Getter<MailMessage, ?>> fields) {
		fields.putAll(FIELDS);
		fields.put("mimeMessage", new Getter<MailMessage, DBObject>() {
			public DBObject get(MailMessage target) {
				return new MimeMessageDBObject( target.getMimeMessage(), getDb() );
			}
		} );
		
		super.configureFields(fields);
	}

}
