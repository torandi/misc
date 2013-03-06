import java.io.*;
import java.security.*;
import java.security.cert.*;


public class Certificate {
	X509Certificate cert = null;

	KeyStore ks = null;
	public Certificate() {
		try{
			
			ks = KeyStore.getInstance("JKS", "SUN");
		}
		catch(KeyStoreException e){
			System.out.println(e.getMessage());
		}
		catch(NoSuchProviderException e){
			System.out.println(e.getMessage());
		}
		InputStream is = null;
		try{
			is = new FileInputStream(new File("/home/torandi/.keystore"));
		}
		catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
		try{
			ks.load(is,"derpherp".toCharArray());
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		catch(NoSuchAlgorithmException e){
			System.out.println(e.getMessage());
		}
		catch(CertificateException e){
			System.out.println(e.getMessage());
		}
	}
	
	public X509Certificate cert() {
		return cert;
	}
	
	public KeyStore keystore() {
		return ks;
	}
}
