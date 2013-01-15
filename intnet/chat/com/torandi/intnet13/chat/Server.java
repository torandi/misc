package com.torandi.intnet13.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.torandi.intnet13.net.ServerSocket;
import com.torandi.intnet13.net.Socket;
import com.torandi.intnet13.net.SocketListener;

public class Server implements SocketListener {
	private int port;
	private ServerSocket server;
	private ArrayList<ClientHandler> clients;
	private HashSet<String> names;
	
	public Server(int port) {
		this.port = port;
		clients = new ArrayList<ClientHandler>();
		names = new HashSet<String>();
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		System.out.printf("Listening on port %d\n", port);
		server.listen(this, true);
	}
	
	public void broadcast(Message msg) {
		for(ClientHandler c : clients) {
			c.send(msg);
		}
	}
	
	public boolean name_available(String name) {
		synchronized (names) {
			return !names.contains(name);
		}
	}
	
	public void change_name(String name, String new_name) {
		synchronized (names) {
			names.remove(name);
			names.add(new_name);
		}
	}
	
	public void unregister_name(String name) {
		synchronized (names) {
			names.remove(name);
		}
	}
	
	public boolean register_name(String name) {
		synchronized (names) {
			return names.add(name);
		}
	}
	
	public void remove_client(ClientHandler c) {
		synchronized (clients) {
			clients.remove(c);
		}
	}
	
	public final HashSet<String> names() {
		return names;
	}
 
	@Override
	public void dataRecived(String data, Socket sck) {
		
	}

	@Override
	public void newClient(Socket client, ServerSocket srvr) {
		synchronized (clients) {
			System.out.println("New connection from "+client.getRemoteSocketAddress().toString());
			clients.add(new ClientHandler(client, this));
		}
	}

	@Override
	public void connectionClosed(Socket sck) {	}
}
