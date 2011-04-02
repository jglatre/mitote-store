package com.github.mitote.store.mongo.convert;

import static javax.mail.Message.RecipientType.TO;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.mongodb.DB;


public class MimeMessageDBObject extends PartDBObject<MimeMessage> {

	private static final Map<String, Getter<MimeMessage, ?>> FIELDS = new LinkedHashMap<String, Getter<MimeMessage, ?>>();
	static {
		FIELDS.put( "_size", new Getter<MimeMessage, Integer>() {
			public Integer get(MimeMessage message) throws Exception {
				return message.getSize();
			}			
		} );
		FIELDS.put( "Date", new Getter<MimeMessage, Date>() {
			public Date get(MimeMessage message) throws Exception {
				return message.getSentDate();
			}			
		} );
		FIELDS.put( "From", new Getter<MimeMessage, List<?>>() {
			public List<?> get(MimeMessage message) throws Exception {
				return new AddressListDBObject( message.getFrom() );
			}			
		} );
		FIELDS.put( "To", new Getter<MimeMessage, List<?>>() {
			public List<?> get(MimeMessage message) throws Exception {
				return new AddressListDBObject( message.getRecipients(TO) );
			}			
		} );
		FIELDS.put( "Subject", new Getter<MimeMessage, String>() {
			public String get(MimeMessage message) throws Exception {
				String subject = message.getSubject();
				return subject != null ? MimeUtility.decodeText( subject ) : null;
			}			
		} );
	}
	
	
	public MimeMessageDBObject(MimeMessage mimeMessage, DB db) { 
		super(mimeMessage, db);
	}


	@Override
	protected void configureFields(Map<String, Getter<MimeMessage, ?>> fields) {
		fields.putAll(FIELDS);
		super.configureFields(fields);
	}
	
}
