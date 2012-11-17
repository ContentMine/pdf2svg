package org.xmlcml.graphics.pdf2svg.raw;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class TestNet {

	public static void main(String[] args) throws Exception {
//		URL url = new URL("http://193.60.92.110");
		URL url = new URL("http://173.194.38.167");
		Object obj = url.getContent();
		System.out.println(obj.getClass());
		InputStream is = (InputStream)obj;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		while (true) {
			String line = br.readLine();
			if (line == null) break;
			System.out.println(line);
		}
	}
}
