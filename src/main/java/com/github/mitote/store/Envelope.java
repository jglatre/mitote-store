package com.github.mitote.store;

import java.util.Date;
import java.util.List;


public interface Envelope {
	Date getTimestamp();
	String getHeloHost();
	String getSender();
	List<String> getRecipients();
}
