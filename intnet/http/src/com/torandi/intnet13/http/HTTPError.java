package com.torandi.intnet13.http;

public class HTTPError extends Exception {
	public final int code;
	public final String code_description;
	
	public HTTPError(int code, String code_description) {
		super("");
		this.code = code;
		this.code_description = code_description;
	}
	
	public HTTPError(int code, String code_description, String message) {
		super(message);
		this.code = code;
		this.code_description = code_description;
	}
}
