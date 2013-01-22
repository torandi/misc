package com.torandi.intnet13.http;

public class ErrorPage implements Page {
	String message;
	String description;
	int error_code;
	
	public ErrorPage(HTTPError error) {
		message = error.getMessage();
		description = error.code_description;
		error_code = error.code;
	}

	@Override
	public String html(WebServer.Request server) {
		return null; //Not used
	}

	@Override
	public String attr(String val) {
		switch(val) {
			case "code":
				return ""+ error_code;
			case "description":
				return description;
			case "message":
				return message;
			default:
				return null;
		}
	}

}
