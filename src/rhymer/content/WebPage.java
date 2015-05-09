package rhymer.content;

import java.net.URL;

public class WebPage implements Content{

	private URL url;
	private String domain;
	private String contents; 
	
	public WebPage(URL url, String contents){
		this.url = url;
		this.domain = extractDomain(url);
		this.contents = contents;
	}
	
	private String extractDomain(URL url) {
		String urlString = url.toString();
		// get what's in between www and .
		return urlString.substring(urlString.indexOf("www"), urlString.indexOf('.'));
	}

	public String asString(){
		return contents;
	}
	
	public String getDomain(){
		return domain;
	}
	
	public URL getURL(){
		return url;
	}
	
}
