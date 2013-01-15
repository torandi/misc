package com.torandi.intnet13.chat;

public class Message {
	private String cmd;
	private String message;
	private static final String split_by = "\1";
	
	public Message(String cmd, String message) {
		this.cmd = cmd;
		this.message = message;
	}
	
	public static Message parse(String raw_line) throws ParseException {
		String[] split = raw_line.split(split_by,2);
		if(split.length != 2) {
			throw new ParseException("Failed to parse line "+raw_line);
		}
		return new Message(split[0], split[1]);
	}
	
	public final String raw() {
		return cmd + split_by + message;
	}
	
	public boolean isCmd(String test) {
		return cmd.equals(test);
	}

	public final String getCmd() {
		return cmd;
	}

	public final String getMessage() {
		return message;
	}
	
	@SuppressWarnings("serial")
	public static class ParseException extends Exception {
		public ParseException(String msg) {
			super(msg);
		}
	}
}
