package com.torandi.intnet13.http_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	String server;
	int port;
	String sessionid = null;
	
	public static void main(String args[]) {
		if(args.length != 3) {
			System.err.println("Required argument: url port");
		}
		Main main = new Main(args[0],Integer.parseInt(args[1]));
		main.run(Integer.parseInt(args[2]));
	}
	
	public Main(String server, int port) {
		this.server = server;
		this.port = port;
	
	}
	
	private HttpURLConnection open(int guess) {
		URL url = null;
		try {
			url = new URL("http", server, port, "/?guess="+guess);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		con.setRequestProperty("User-Agent", "Java 7");
		
		if(sessionid != null) {
			con.setRequestProperty("Cookie", "SESSIONID="+sessionid);
		}
		
		try {
			con.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String cookies = con.getHeaderField("Set-Cookie");
		String split[] = cookies.split("=");
		if(split[0].equals("SESSIONID")) {
			sessionid = split[1];
		}
		
		return con;
	}
	
	private String read(HttpURLConnection con) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		StringBuilder str = new StringBuilder();
		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				str.append(line + "\n");
			}
			reader.close();
			return str.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public void run(int turns) {
		final Pattern num_guesses_pattern = Pattern.compile("Number of guesses: ([0-9]+)");
		double avg_guesses = 0.0;
		for(int i=0; i<turns; ++i) {
			System.out.print("Turn "+(i+1)+": ");
			boolean correct = false;
			int min = 1; 
			int max = 100;
			while(!correct) {
				if(min > max) {
					System.err.println("IMPOSSIBRU! min > max");
					System.exit(-1);
				}
				int guess = min + ((max - min) / 2);
				String res = read(open(guess));
				if(res.contains("Correct")) {
					correct = true;
					Matcher m = num_guesses_pattern.matcher(res);
					if(m.find()) {
						int num_guesses = Integer.parseInt(m.group(1));
						System.out.println(num_guesses + " guesses");
						avg_guesses += num_guesses / (double)turns;
					} else {
						System.err.println("Critical error: Can't read number of guesses, pattern doesn't match");
						System.err.println("Body: "+res);
						System.exit(-1);
					}
				} else if(res.contains("Guess lower")) {
					max = guess - 1;
				} else if(res.contains("Guess higher")) {
					min = guess + 1;
				} else {
					System.err.println("Can't find state");
					System.err.println("Body: "+res);
					System.exit(-1);
				}
			}
		}
		System.out.println("Average: "+avg_guesses);
	}
}
