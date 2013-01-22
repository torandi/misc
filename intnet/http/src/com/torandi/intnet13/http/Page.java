package com.torandi.intnet13.http;

public interface Page {
	/*
	 * Shall return the html for this page
	 */
	public String html(WebServer.Request request) throws HTTPError;
	
	public String attr(String val) throws HTTPError;
}
