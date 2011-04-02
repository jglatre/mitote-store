package com.github.mitote.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class MailMessage implements Envelope {

	private static Session session = Session.getDefaultInstance(new Properties());

	private Object storeId;
	private Date timestamp;
	private String receivingAgent;
	private String clientHost;
	private InetAddress clientAddress;
	private String heloHost;
	private String sender;
	private List<String> recipients = new ArrayList<String>();
	private MimeMessage mimeMessage;

	
	public MailMessage(Object storeId) {
		this();
		this.storeId = storeId;
	}
	
	
	public MailMessage() {
		this(new MimeMessage(session));
	}
	
	
	public MailMessage(MimeMessage mimeMessage) {
		this.timestamp = new Date();
		this.mimeMessage = mimeMessage;		
	}
		
	
	public Object getStoreId() {
		return storeId;
	}
	
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	
	public String getHeloHost() {
		return heloHost;
	}
	
	
	public void setHeloHost(String heloHost) {
		this.heloHost = heloHost;
	}
	
	
	public String getSender() {
		return sender;
	}

	
	public void setSender(String sender) {
		this.sender = sender;
	}
	

	public List<String> getRecipients() {
		return recipients;
	}
	

	public void addRecipient(String recipient) {
		this.recipients.add(recipient);
	}

	
	public MimeMessage getMimeMessage() {
		return mimeMessage;
	}
	
	
	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}
	
	
	public void setMimeMessage(InputStream in) {
		try {
			setMimeMessage( new MimeMessage(session, in) );
		}
		catch (MessagingException e) {
			e.printStackTrace();
		}
	}
		
	
	public void setHeader(String name, String value) {
		try {
			mimeMessage.addHeader(name, value);
		}
		catch (MessagingException e) {
			throw new RuntimeException("Unable to set header", e);
		}
	}
	
	
	public void setBody(InputStream body) {
    	try {
    		String contentType = mimeMessage.getContentType();
			if (contentType.startsWith("text/plain")) {
				mimeMessage.setContent( streamAsString(body), contentType );
//				mimeMessage.setDataHandler( new DataHandler(new BodyDataSource(contentType, body)) );
    		}
    		else {
    			MimeMultipart multipart = new MimeMultipart( new BodyDataSource(contentType, body) );
    			mimeMessage.setContent( multipart );
    		}
		} 
    	catch (Exception e) {
    		throw new RuntimeException("Unable to set body", e);
		}
	}
	
	
	private static class BodyDataSource implements DataSource {
		final String contentType;
		final InputStream input;

		public BodyDataSource(String contentType, InputStream input) {
			this.contentType = contentType;
			this.input = input;
		}

		public InputStream getInputStream() throws IOException {
			return input;
		}

		public OutputStream getOutputStream() throws IOException {
			throw new UnsupportedOperationException("");
		}

		public String getContentType() {
			return contentType;
		}

		public String getName() {
			return null;
		}
	}

	
	private static String streamAsString(InputStream in) throws IOException {
	    StringBuilder out = new StringBuilder();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n));
	    }
	    return out.toString();
	}

}
