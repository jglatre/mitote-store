package com.github.mitote.store.mongo.convert;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.bson.types.ObjectId;

import com.github.mitote.store.MailMessage;
import com.github.mitote.store.mongo.MongoDbFile;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;


public class DBObjectConverter {
	
	private static Session session = Session.getDefaultInstance(new Properties());
	
	private DB db;
	private GridFS gridFS;
	
	
	public DBObjectConverter(DB db) {
		this.db = db;
	}


	public MailMessage toMailMessage(DBObject dbo) {
		MailMessage mailMessage = new MailMessage( dbo.get("_id") );
		mailMessage.setTimestamp( (Date) dbo.get("timestamp") );
		mailMessage.setHeloHost( (String) dbo.get("helo") );
		mailMessage.setSender( (String) dbo.get("sender") );
		
		if (dbo.containsField("recipients")) {
			for (String recipient : ((List<String>) dbo.get("recipients"))) {
				mailMessage.addRecipient( recipient );
			}
		}

		if (dbo.containsField("mimeMessage")) {
			mailMessage.setMimeMessage( toMimeMessage( (DBObject) dbo.get("mimeMessage") ) );
		}
		
		return mailMessage;
	}
	

	public MimeMessage toMimeMessage(DBObject dbo) {
		MimeMessage mimeMessage = new MimeMessage(session);
		
		try {
			for (String field : dbo.keySet()) {
				if (!field.startsWith("_") && !field.equals("Content")) {
					Object value = dbo.get(field);
					if (value instanceof String) {
						mimeMessage.addHeader( field, MimeUtility.encodeText((String) value) );
					}
					else if (value instanceof Iterable) {
						for (Object item : (Iterable<?>) value) {
							mimeMessage.addHeader( field, MimeUtility.encodeText((String) item) );
						}
					} 
					else if (value instanceof Date) {
						if (field.equals("Date")) {
							mimeMessage.setSentDate( (Date) value );
						}
					}
				}
			}
			
			Object content = dbo.get("Content");
			if (content instanceof String) {
				mimeMessage.setContent( content, mimeMessage.getContentType() );
			}
			else if (content instanceof List) {
				mimeMessage.setContent( toMultipart((List<DBObject>) content) );
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return mimeMessage;
	}
	
	
	public Multipart toMultipart(List<DBObject> list) throws MessagingException {
		MimeMultipart multipart = new MimeMultipart();
		for (DBObject dbo : list) {
			multipart.addBodyPart( toBodyPart(dbo) );
		}
		return multipart;
	}
	
	
	public BodyPart toBodyPart(DBObject dbo) throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();
		
		for (String field : dbo.keySet()) {
			if (!field.equals("Content")) {
				bodyPart.setHeader( field, (String) dbo.get(field) );
			}
		}

		Object content = dbo.get("Content");
		if (content instanceof String) {
			bodyPart.setContent( content, bodyPart.getContentType() );
		}
		else if (content instanceof List) {
			bodyPart.setContent( toMultipart((List<DBObject>) content) );
		}
		else if (content instanceof ObjectId) {
			DataSource ds = new MongoDbFile( getGridFS(), (ObjectId) content );
			bodyPart.setDataHandler( new DataHandler(ds) );
		}

		return bodyPart;
	}
	
	
	public Collection<MailMessage> toMailMessageIterable(DBCursor cursor) {
		return new MailMessageCursor( cursor );
	}
	
	
	protected class MailMessageCursor extends AbstractCollection<MailMessage> {
		private final DBCursor cursor;
		
		public MailMessageCursor(DBCursor cursor) {
			this.cursor = cursor;
		}
		
		public int size() {
			return cursor.count();
		}

		public Iterator<MailMessage> iterator() {
			return new Iterator<MailMessage>() {
				public boolean hasNext() {
					return cursor.hasNext();
				}

				public MailMessage next() {
					return toMailMessage( cursor.next() );
				}

				public void remove() {
					cursor.remove();
				}				
			};
		}		
	}


	protected GridFS getGridFS() {
		if (gridFS == null) {
			gridFS = new GridFS( db );
		}
		return gridFS;
	}

}
