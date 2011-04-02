package com.github.mitote.store;

import java.util.Collection;

import com.github.mitote.store.query.Query;


/**
 * @author juanjo
 */
public interface MailStore {

	/**
	 * Store a new MailMessage.
	 * 
	 * @param message
	 */
	void put(MailMessage message);
	
	/**
	 * Get an existing MailMessage and mark it as taken. A taken message
	 * can not be removed, but can be queried.
	 * 
	 * @param query
	 * @return
	 */
	MailMessage take(Query query);
	
	/**
	 * Release a previously taken MailMessage, i.e. make it available
	 * for removal.
	 * 
	 * @param message
	 */
	void release(MailMessage message);
	
	/**
	 * Get the first MailMessage that satisfies a given Query.
	 * 
	 * @param query
	 * @return
	 */
	MailMessage get(Query query);
	
	/**
	 * Get all MailMessages satisfying a given Query.
	 * 
	 * @param query
	 * @return
	 */
	Collection<MailMessage> find(Query query);
	
	/**
	 * Remove all MailMessages satisfying a given Query.
	 * 
	 * @param query
	 */
	void remove(Query query);
}
