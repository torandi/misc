package com.torandi.intnet13.http;

import java.io.*;

public class WebPageClassLoader extends ClassLoader {

	private File dir;
	
    public WebPageClassLoader(File public_dir) {
    	dir = public_dir;
    }

    public Class<?> findClass(String name) {
        byte[] b = null;
        FileInputStream is = null;
        String tmp = name.replace(".", "/");
        try {
            is = new FileInputStream(new File(dir, tmp + ".class"));
            int bytes = is.available();
            b = new byte[bytes];
            is.read(b);
            is.close();
            return defineClass(name, b, 0, b.length);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}