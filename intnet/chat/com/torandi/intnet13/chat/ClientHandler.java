package com.torandi.intnet13.chat;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import com.torandi.intnet13.chat.Message.ParseException;
import com.torandi.intnet13.net.ServerSocket;
import com.torandi.intnet13.net.Socket;
import com.torandi.intnet13.net.SocketListener;

public class ClientHandler implements SocketListener{
	private Socket client;
	private Server server;
	private String name = null;
	
	public ClientHandler(Socket client_socket, Server server) {
		this.client = client_socket;
		this.server = server;
		client.receive(this);
	}

	public void send(Message msg) {
		client.out().println(msg.raw());
	}
	
	public void disconnect() {
		if(name != null) server.unregister_name(name);
		if(client.isConnected()) {
			try {
				client.stop_recieve();
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		server.remove_client(this);
		server.broadcast(new Message("USER_QUIT",name));
	}
	
	@Override
	public void dataRecived(String data, Socket sck) {
		try {
			Message msg = Message.parse(data);
			if(msg.isCmd("NICK")) {
				if(name != null && name.equals(msg.getMessage())) {
					send(new Message("NICK", "OK"));
					return;
				}
				
				if(server.name_available(msg.getMessage())) {
					if(name != null) {
						server.change_name(name, msg.getMessage());
					} else {
						server.register_name(msg.getMessage());
					}
					name = msg.getMessage();
					send(new Message("NICK","OK"));
				} else {
					send(new Message("NICK","ERR"));
				}
				return;
			} else if(msg.isCmd("QUIT")) {
				disconnect();
			} else if(msg.isCmd("NAMES")) {
				StringBuilder names = new StringBuilder();
				for(String n : server.names()) {
					names.append(n + "\t");
				}
				send(new Message("NAMES",names.toString()));
			} else if(msg.isCmd("MSG")) {
				if(name != null) {
					server.broadcast(new Message("MSG",new Message(name, msg.getMessage()).raw()));
				} else {
					send(new Message("ERR","Set a nick before sending a message!"));
				}
			}
		} catch(ParseException e) {
			System.out.println("["+client_id()+"]: Invalid message received: "+data);
		}
	}
	
	public final String client_id() {
		if(name != null) {
			return name;
		} else {
			return "client "+client.getRemoteSocketAddress().toString();
		}
	}

	@Override
	public void newClient(Socket client, ServerSocket srvr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosed(Socket sck) {
		disconnect();
	}
}
