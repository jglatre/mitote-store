package com.github.mitote.store.util;

import java.io.File;
import java.io.FileInputStream;

import com.github.mitote.store.MailMessage;
import com.github.mitote.store.MailStore;
import com.github.mitote.store.mongo.MongoDbStore;


public class MaildirDigester {

	private File dirBase;
	private MailStore store;

	
	public void run() {
		for (File f : dirBase.listFiles()) {
			if (f.isFile() && f.canRead()) {
				System.out.println("processing " + f.getName());
				try {
					MailMessage message = new MailMessage();
					message.setMimeMessage( new FileInputStream(f) );
					store.put(message);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		MaildirDigester digester = new MaildirDigester();
		digester.dirBase = new File("/tmp/gmail-inbox");
		digester.store = new MongoDbStore();
		
		digester.run();
	}

}
