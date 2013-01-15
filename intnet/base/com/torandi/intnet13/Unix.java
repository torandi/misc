package com.torandi.intnet13;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * since java can't do anything correct with the terminal at all, we have to do it ourselves..
 * @author torandi
 *
 */
public class Unix {
	private static String ttyConfig; //for recrapping java when done
	
	public static String stty(final String args) throws IOException, InterruptedException {
		String cmd = "stty " + args + " < /dev/tty";

		return exec(new String[] {
				"sh",
				"-c",
				cmd
		});
	}
	
    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    public static String exec(final String[] cmd)
                    throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        String result = new String(bout.toByteArray());
        return result;
    }
    
    public static void uncrap_java() {
    	try {
	        ttyConfig = stty("-g");
	
	        // set the console to be character-buffered instead of line-buffered
	        stty("-icanon min 1");
	
	        // disable character echoing
	        stty("-echo");	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void recrap_java() {
    	try {
			stty( ttyConfig.trim() );
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
