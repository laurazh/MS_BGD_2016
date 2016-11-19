import java.io.BufferedReader; 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Test;


public class Preparation {
	/**
	 * remove empty lines, any character which is not alphanumerical
	 * @param args name of the file to clean
	 * @return file_cleaned.txt
	 * @throws IOException 
	 */

	public static  void FileCleaning(String args) throws IOException {
		//Scanner file;

		FileOutputStream outputStream;
		outputStream = new FileOutputStream("file_cleaned.txt");
		PrintWriter writer= new PrintWriter(outputStream);

		try {
			//read the file that 
			Path file = Paths.get(args);
			List<String> lines = Files.readAllLines(file,Charset.forName("UTF-8"));
			for (int i=0; i<lines.size();i++) {
				if (!lines.get(i).isEmpty()){
					/*
				line = line.toLowerCase();
				line = Normalizer.normalize(line, Normalizer.Form.NFD);
				line = line.replaceAll("[^\\p{ASCII}]", "");
				line = line.replaceAll("[^a-z]", " ");
					 * */
					//					String before=null; 
					//					String after=null; 
					//					String[] dummy = {",",";","!"," le "," à "," la ", " les ", 
					//							" on "," de "," que "," qui "," quoi "
					//					};
					//					for (Iterator<String> iter = list.listIterator(); iter.hasNext(); ) {
					//					    String a = iter.next();
					//					    if (...) {
					//					        iter.remove();
					//					    }
					//					}
					//lines.get(i).replace("é","e");
					String alpha =lines.get(i).replace("é","e");
					alpha =alpha.replace("è","e");

					String[]To_remove={"à ",":"  };
					String[]To_replace ={" le ", " la ", " les ", " de ", " des "," au ",
							" ou ", " ne ", " aux ", " et " };
					for (String dlt : To_remove){
						alpha = alpha.replace(dlt, " "); 
					}
					alpha = alpha.replaceAll("[^a-zA-Z1-9 ]", ""); 
					writer.write(alpha);
					writer.write("\n"); 

				}

			}
			writer.close();


			//			try {
			//				liste_machines_ok = (ArrayList<String>) Files.readAllLines(file,Charset.forName("UTF-8"));
			//			} catch (IOException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}

			//			file = new Scanner(new File(args));
			//			writer = new PrintWriter("file_cleaned.txt");
			//			while (file.hasNext()) {
			//				String line = file.nextLine();
			//				if (!line.isEmpty()) {
			//
			//					line = Normalizer.normalize(line, Normalizer.Form.NFD);
			//					line = line.replaceAll("[^\\p{ASCII}]", "");
			//					//String alpha = line.replaceAll("[^a-zA-Z1-9éè ]", "");  
			//					String alpha=null;
			//					String[] dummy = {",",";","!"," le ", " la ", " les ", " on "," de "," que "," qui ",
			//							" quoi "};
			//					for(String replacement: dummy) {
			//						alpha= line.replace(replacement," ");
			//					}
			//
			//					writer.write(alpha);
			//					writer.write("\n");
			//				}
			//			}

			//			file.close();
			//			writer.close();

		} catch (FileNotFoundException ex) {
			Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Slice the file per line into an array list
	 * @param filename
	 * @return array list of the text lines
	 */
	public static  ArrayList<String> FileSlicer(String filename,String Sx, ArrayList<String> Sxtable ) {

		try{
			InputStream flux=new FileInputStream(filename);
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;
			while ((ligne=buff.readLine())!=null){ 
				Sx = ligne;
				Sxtable.add(Sx);
			}
			buff.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		} 

		return Sxtable;
	}


}

/**
//remove any coma, question mark....apres le split
String adjusted = text.replaceAll("(?m)^[ \t]*\r?\n", "");
String clean = input.replaceAll("[, ;]", "");

String alpha = word.replaceAll("[^a-zA-Z1-9]", "");
//------------    
LineNumberReader  lnr = new LineNumberReader(new FileReader(new File("File1")));
lnr.skip(Long.MAX_VALUE);
System.out.println(lnr.getLineNumber() + 1); //Add 1 because line index starts at 0
//Finally, the LineNumberReader object should be closed to prevent resource leak
lnr.close();

int nb_lines =lnr.getLineNumber() + 1
int nb_machine_use=4
int nb_file_send_to_one_machine=0
if(nb_lines/nb_machine_use)
 **/