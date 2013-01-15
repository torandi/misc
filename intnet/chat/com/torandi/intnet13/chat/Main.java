package com.torandi.intnet13.chat;

import com.torandi.intnet13.GetOpt;
import com.torandi.intnet13.StringWrapper;
import com.torandi.intnet13.GetOpt.ArgumentException;
import com.torandi.intnet13.GetOpt.Option.arg_style;
import com.torandi.intnet13.GetOpt.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean client_mode = true;
		int port = Network.DEFAULT_PORT;
		
		final Option[] options = { 
				new Option("client", 'c', arg_style.NO_ARGUMENT, "Start in client mode (default)"),
				new Option("server", 's', arg_style.NO_ARGUMENT, "Start in server mode"),
				new Option("port", 'p', arg_style.REQUIRED_ARGUMENT, "Port for communications (default "+Network.DEFAULT_PORT + ")"),
				new Option("help", 'h', arg_style.NO_ARGUMENT, "This text")
		};
		final GetOpt getopt = new GetOpt(options);
		
		ParsePair parse_pair = new ParsePair(args);
		StringWrapper arg = new StringWrapper();
		try {
			char opt = (char)-1;
			while((opt = getopt.parse(parse_pair, arg)) != (char)-1) {
				switch(opt) {
				case 'c':
					client_mode = true;
					break;
				case 's':
					client_mode = false;
					break;
				case 'p':
					port = Integer.parseInt(arg.str);
					break;
				case 'h':
					print_usage(getopt);
					return;
				default:
				}
			}
		} catch (ArgumentException e) {
			System.out.println(e.getMessage());
			print_usage(getopt);
			return;
		}
		if(client_mode) {
			if(parse_pair.has_current()) {
				String server = parse_pair.next();
				new Client(server, port).start();
			} else {
				System.out.println("Missing required argument server");
				print_usage(getopt);
				return;
			}
		} else {
			new Server(port).start();
		}
		
	}
	
	public static void print_usage(final GetOpt getopt) {
		System.out.println("Usage: chat [options] server (if client mode):\noptions:");
		getopt.print_usage();
		
	}
}
