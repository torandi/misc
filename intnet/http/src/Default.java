import java.util.Random;

import com.torandi.intnet13.http.HTTPError;
import com.torandi.intnet13.http.Page;
import com.torandi.intnet13.http.WebServer.Request;


public class Default implements Page {

	private String message = "";
	private String image = "";
	private String form = "guess_form";
	private Request request;
	
	
	@Override
	public String html(Request request) throws HTTPError {
		this.request = request;
		String stored = request.get_data("target");
		int target;
		if(stored == null) {
			target = new Random().nextInt(100) + 1;
			request.set_data("target", ""+target);
		} else {
			target = Integer.parseInt(stored);
		}
		
		String guess_str = request.request("guess");
		
		if(guess_str != null) {
			int guess = Integer.parseInt(guess_str);
			if(guess == target) {
				message = "<h2>Correct!!!</h2>";
				image = "<p><img src='correct.gif'/></p>";
				form = "new_guess";
				request.set_data("target", null);
			} else {
				if(guess < target) {
					message = "<h2>Wrong! Guess higher.</h2>";
				} else {
					message = "<h2>Wrong! Guess lower.</h2>";
				}
				image = "<p><img src='nope" + (new Random().nextInt(2) + 1) + ".jpg'/></p>";
			}
		} else {
			message = "<h2>Guess a number between 1 and 100</h2>";
		}
		
		return request.view("guess", this);
	}

	@Override
	public String attr(String val) throws HTTPError {
		switch(val) {
		case "message":
			return message;
		case "image":
			return image;
		case "form":
			return request.view(form, this);
		default:
			return null;
		}
	}

}
