package com.github.mitote.store.mongo.convert;

import java.util.AbstractList;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;


public class AddressListDBObject extends AbstractList<String> {

	private final Address[] addresses;
	 
	
	public AddressListDBObject(Address[] addresses) {
		this.addresses = addresses;
	}
	
	
	@Override
	public String get(int index) {
		if (addresses[ index ] instanceof InternetAddress) {
			return ((InternetAddress) addresses[ index ]).toUnicodeString();
		}
		else {
			return addresses[ index ].toString();
		}
	}

	
	@Override
	public int size() {
		return addresses == null ? 0 : addresses.length;
	}		
}
