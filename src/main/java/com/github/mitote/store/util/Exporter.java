package com.github.mitote.store.util;

import org.bson.types.ObjectId;

import com.github.mitote.store.MailMessage;
import com.github.mitote.store.MailStore;
import com.github.mitote.store.mongo.MongoDbStore;
import com.github.mitote.store.query.Equals;
import com.github.mitote.store.query.Order;
import com.github.mitote.store.query.Query;


public class Exporter {

	private MailStore store;

	
	public void run() {
		Iterable<MailMessage> messages = store.find( 
				new Query( 
						new Equals<ObjectId>("_id", new ObjectId("4c478af5eec1e13022d63666")), 
						new Order("Date", false) 
						) 
				);
		
		try {
			for (MailMessage message : messages) {
				message.getMimeMessage().writeTo( System.out );
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		Exporter exporter = new Exporter();
		exporter.store = new MongoDbStore();

		exporter.run();
	}

}
