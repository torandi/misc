import javax.net.ssl.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

public class Server{
	static Certificate cert;
	public static void main(String[] args){
		cert = new Certificate();
		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e1) {
			System.err.println("Fail :(");
			return;
		}
		KeyManagerFactory keyManagerFactory;
		try {
			keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		try {
			keyManagerFactory.init(cert.keystore(), "derpherp".toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException
				| NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		// init KeyManager
		KeyManager keyManagers[] = keyManagerFactory.getKeyManagers();

		try {
			sslContext.init(keyManagers, null, new SecureRandom());
		} catch (KeyManagementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		SSLServerSocketFactory ssf = (SSLServerSocketFactory)sslContext.getServerSocketFactory();
		
		System.out.println("Stöder:");
		for(int i = 0; i < ssf.getSupportedCipherSuites().length; i++)
			System.out.println(ssf.getSupportedCipherSuites()[i]);

		SSLServerSocket ss = null;

		try {
			ss = (SSLServerSocket)ssf.createServerSocket(1234);
		} catch (IOException e) {
			System.err.println("Failed to create socket");
			return;
		}
		
		while(true) {
			try{
				SSLSocket s = (SSLSocket)ss.accept();
				BufferedWriter write = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				write.write("HTTP/1.1 200 OK\nConnection: close\nContent-Type: text/html; charset=UTF-8\n\nDerp Herp\n");
				write.flush();
				s.close();
			} catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}
}
