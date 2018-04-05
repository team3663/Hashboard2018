package controlpanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.Instant;
/**
 * @author Trent
 * Unworking and unused
 */
public class FileLogger {
	PrintWriter writer = null;
	public FileLogger(String savePath) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
		new File(savePath).mkdirs();
		new File(savePath + "\\" + Instant.now()).mkdirs();
		System.out.println(savePath + "\\" + Instant.now());
		try {
			writer = new PrintWriter(savePath + "\\" + Instant.now() + ".txt","UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.err.println("THE PRINTWRITER FAILED TO INITIALIZE");
			//System.exit(0);
		}
	}
	
	public void writeLine(String text) {
		if(writer != null) {
			writer.println(text);
		}
	}
	
	public void close() {
		writer.close();
	}
}
