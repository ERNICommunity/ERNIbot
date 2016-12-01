package de.erni.confluenceapi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Tools {

	public static void writeFile(String filename, String content) {
		File f = new File(filename);

		StringBuffer sb = new StringBuffer(content);
		try {
			FileWriter fwriter = new FileWriter(f);
			BufferedWriter bwriter = new BufferedWriter(fwriter);
			bwriter.write(sb.toString());
			bwriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
