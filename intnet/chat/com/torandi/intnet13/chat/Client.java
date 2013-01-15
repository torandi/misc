package com.torandi.intnet13.chat;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.torandi.intnet13.Unix;
import com.torandi.intnet13.chat.Message.ParseException;
import com.torandi.intnet13.net.ServerSocket;
import com.torandi.intnet13.net.Socket;
import com.torandi.intnet13.net.SocketListener;

public class Client implements SocketListener {
	private final String server;
	private final int port;
	private Socket socket;
	private boolean nick_set = false;
	private int input_length = 0;
	private Reader input;
	private String current_input = "";
	private SimpleDateFormat date_format = new SimpleDateFormat("[HH:mm:ss]");
	
	Client(String server, int port) {
		this.server = server;
		this.port = port;
	}
	
	void start() {
		System.out.printf("Connecting to %s:%d\n", server, port);
		try {
			socket = new Socket(server,port);
			socket.receive(this);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		if(socket.isConnected()) {
			System.out.println("Connected to server.");
			print_prompt();
			get_input();
			
		} else {
			System.out.println("Failed to connect to server");
			System.exit(1);
		}
	}
	
	private void print_line(String line) {
		synchronized (this) {
			clear_input();
			System.out.print(date_format.format(new Date())+" ");
			System.out.println(line);
			synchronized (current_input) {
				print_prompt();
			}
		}
	}

	private void clear_input() {
		System.out.print("\r");
		for(int i=0;i < (input_length + current_input.length()) ; ++i) {
			System.out.print(" ");
		}
		input_length = 0;
		System.out.print("\r");
	}
	
	private void print_prompt() {
		String prompt;
		if(!nick_set) {
			prompt = "-- Enter name: ";
		} else {
			prompt = ">> ";
		}
		System.out.print(prompt);
		input_length = prompt.length();
		System.out.print(current_input);
	}
	
	private void get_input() {
		/*
		 * This wont work on windows, 
		 * but since java is I crappy language anyways I don't care.
		 * At all.
		 * Mongospråk.
		 */
		Unix.uncrap_java();

		input = System.console().reader();

		while(socket.isConnected()) {
			int r;
			try {
				if((r = input.read()) != -1) {
					char ch = (char) r;
					synchronized (current_input) {
						if(ch != '\n') {
							switch(r) {
							case 4: //Ctrl+D
								disconnect();
								System.out.println("\n");
								return;
							case 21: //Ctrl + U:
								clear_input();
								current_input = "";
								print_prompt();
								break;
							case 127:
								//Backspace
								if(current_input.length() > 0) {
									current_input = current_input.substring(0, current_input.length() - 1);
									System.out.print("\b \b");
								}
								break;
							default:
								current_input += ch;
								System.out.print(ch);
								System.out.flush();
							}
						} else {
							if(!nick_set) {
								send(new Message("NICK",current_input));
							} else if(current_input.trim().equals("/names")) {
								send(new Message("NAMES",""));
							} else if(!current_input.isEmpty()){
								send(new Message("MSG",current_input));
							}
							clear_input();
							current_input = "";
							print_prompt();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Unix.recrap_java();
	}

	public void send(Message msg) {
		socket.out().println(msg.raw());
	}
	
	public void disconnect() {
		if(socket.isConnected()) {
			try {
				socket.stop_recieve();
				send(new Message("QUIT",""));
				Thread.sleep(100);
				socket.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void dataRecived(String data, Socket sck) {
		try {
			Message msg = Message.parse(data);
			if(msg.isCmd("NICK")) {
				if(msg.getMessage().equals("OK")) {
					nick_set = true;
					print_line("Nick accepted");
				} else {
					print_line("Nick in use");
				}
			} else if(msg.isCmd("QUIT")) {
				disconnect();
			} else if(msg.isCmd("NAMES")) {
				print_line("[Users]\n"+msg.getMessage());
			} else if(msg.isCmd("USER_QUIT")) {
				print_line("  "+msg.getMessage()+ " has quit.");
			} else if(msg.isCmd("MSG")) {
				Message m2 = Message.parse(msg.getMessage());
				print_line("< " + m2.getCmd() + " > "+m2.getMessage());
			}
		} catch(ParseException e) {
			System.out.println("Invalid message received: "+data);
		}
		
	}

	@Override
	public void newClient(Socket client, ServerSocket srvr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed(Socket sck) {
		// TODO Auto-generated method stub
		
	}
}
