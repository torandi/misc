package com.torandi.intnet13.http;

import java.io.IOException;

import com.torandi.intnet13.GetOpt;
import com.torandi.intnet13.StringWrapper;
import com.torandi.intnet13.GetOpt.*;
import com.torandi.intnet13.GetOpt.Option.arg_style;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 80;
		String directory = ".";
		
		final Option[] options = {
			new Option("root", 'r', arg_style.REQUIRED_ARGUMENT, "Root directory"),
			new Option("port",'p',arg_style.REQUIRED_ARGUMENT, "Port to listen on")	
		};
		
		final GetOpt getopt = new GetOpt(options);
		
		ParsePair parse_pair = new ParsePair(args);
		StringWrapper arg = new StringWrapper();
		try {
			char opt = (char)-1;
			while((opt = getopt.parse(parse_pair, arg)) != (char)-1) {
				switch(opt) {
				case 'p':
					port = Integer.parseInt(arg.str);
					break;
				case 'r':
					directory = arg.str;
					break;
				case 'h':
					print_usage(getopt);
					System.exit(0);
				default:
				}
			}
		} catch (ArgumentException e) {
			System.out.println(e.getMessage());
			print_usage(getopt);
			return;
		}
	
		WebServer server = null;
		
		try {
			server = new WebServer(port, directory);
		} catch (Exception e) {
			System.err.println("Failed to create webserver: "+e.getMessage());
			System.exit(-1);
		}
		server.run();
	}
	
	public static void print_usage(final GetOpt getopt) {
		System.out.println("Usage: http [options]\noptions:");
		getopt.print_usage();
		
	}

}
