package com.github.mitote.store.mongo.convert;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.AbstractList;
import java.util.Enumeration;
import java.util.Map;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;

import com.github.mitote.store.mongo.MongoDbFile;
import com.mongodb.DB;


public class PartDBObject<T extends Part> extends WrapperDBObject<T> {
	
	public PartDBObject(T part, DB db) {
		super(part, db);
	}

	
	protected void configureFields(Map<String, Getter<T, ?>> fields) {
		try {
			Enumeration<?> headers = getTarget().getAllHeaders();
			while (headers.hasMoreElements()) {
				Header header = (Header) headers.nextElement();
				String name = header.getName();
				if (!fields.containsKey(name)) {
					fields.put(name, new HeaderGetter(name));
				}
			}
		} 
		catch (MessagingException e) {
			e.printStackTrace();
		}
		
		fields.put("Content", new ContentGetter());
	}

	
	protected class HeaderGetter implements Getter<T, Object> {
		private final String name;
		
		public HeaderGetter(String name) {
			this.name = name;
		}
		
		public Object get(T target) throws Exception {
			String[] headers = target.getHeader(name);
			if (headers == null || headers.length == 0) {
				throw new RuntimeException();
			}
			return headers.length == 1 ? 
					MimeUtility.decodeText( headers[0] ) : new DecoderTextList( headers );
		}		
	};
	
	
	protected class ContentGetter implements Getter<T, Object> {
		public Object get(T part) throws Exception {
			Object content = part.getContent();
			if (content instanceof Multipart) {
				return new MultipartDBObject( (Multipart) content, getDb() );
			}
			else if (content instanceof InputStream) {
				MongoDbFile file = new MongoDbFile( getGridFS(), part.getInputStream(), part.getContentType(), part.getFileName() );
				return file.getId();
			}
			else {
				return content;
			}
		}		
	}
	
	
	public static class DecoderTextList extends AbstractList<String> {
		private final String[] texts;
		
		public DecoderTextList(String[] texts) {
			this.texts = texts;
		}

		@Override
		public String get(int index) {
			try {
				return MimeUtility.decodeText( texts[ index ] );
			}
			catch (UnsupportedEncodingException e) {
				throw new RuntimeException();
			}
		}

		@Override
		public int size() {
			return texts.length;
		}		
	}
	
}
