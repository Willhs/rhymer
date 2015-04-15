package rhymer;

import java.io.File;

public class Main {

	private static final String TEXT_PATH = "text" + File.separator;

	public static void main(String[] args){
		new Rhymer(TEXT_PATH + "computer-wiki.txt");
	}
}
