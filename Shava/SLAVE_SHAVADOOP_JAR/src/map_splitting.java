
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;  
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


public class map_splitting {

	public static void main(String[]  args) throws IOException {  
		//arg[0] mode modeUMXSMX ou modeSXUMX
		//if modeSXUMX => arg[1] le fichier en entrée 
		if (args[0].equals("modeSXUMX")){
			String input_value = null;
			try{ 

				Path file = Paths.get(args[1]+".txt");
				List<String> lines = Files.readAllLines(file,Charset.forName("UTF-8"));
 				for (int i=0; i<lines.size();i++) {
					if (!lines.get(i).isEmpty()){
						input_value=lines.get(i);
						
					} 
				} 
				
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}


			//String input_value = args[0];
			String[] words = input_value.split("\\s+");

			ArrayList<String> Map_Umx = new ArrayList<String>();

			//List<String> Map_Umx = new ArrayList<String>();
			for (int i = 0; i < words.length; i++) { 
				words[i] = words[i].replaceAll("[^\\w]", "");
				Map_Umx.add(words[i]+ " 1");  
			}

			String title = args[1].substring(1, args[1].length()); 
			
			FileOutputStream outputStream;
			outputStream = new FileOutputStream("UM"+ title+".txt");
			PrintWriter writer= new PrintWriter(outputStream);
 

			for (int i = 0; i < Map_Umx.size(); i++){ 
				writer.write(Map_Umx.get(i));
				writer.write("\n");
			} 
			writer.close();

			HashMap<String,Integer> cle_UMX = new HashMap<String,Integer>(); 
			for (int i = 0; i < words.length; i++) { 
				words[i] = words[i].replaceAll("[^\\w]", "");
				cle_UMX.put(words[i], 1);
			}
			//Sortie standard ne pas supprimé
			for ( String key : cle_UMX.keySet() ) {
				System.out.println( key );
			}
		} 
		///////////////////////////////////////////
		//if modeSXUMX => arg[1] key word to count
		//if modeSXUMX => arg[2] SMx
		//if modeSXUMX => arg[3] et arg[4] UMx to read
		
		List<String> words = new ArrayList <String>();

		//choice of mode
		if (args[0].equals("modeUMXSMX")){ 
			String input_value = null; 
			try{ 
				//read the UMx
				for (int i=3;i < args.length; i++){
					
					Path file = Paths.get(args[i]+".txt");
					List<String> lines = Files.readAllLines(file,Charset.forName("UTF-8"));				
//					InputStream flux=new FileInputStream(args[i]+".txt"); 
//					InputStreamReader lecture=new InputStreamReader(flux);
//					BufferedReader buff=new BufferedReader(lecture);
//					String ligne;			
					
					for (int j=0; j<lines.size();j++) {
						//read lines if not empty
						if (!lines.get(j).isEmpty()){
							//store the value in values
							input_value=lines.get(j);
							//System.out.println(input_value.split(" ")[0].toUpperCase() + " " +args[1].toUpperCase());
							 if(input_value.split(" ")[0].toUpperCase().equals( args[1].toUpperCase())){ 				
									words.add(input_value); 
								}
						}
					}

				}
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}
 
			
			//dictionnary linking UMx and SMx
			HashMap<String,Integer> Umx_Smx = new HashMap<String,Integer>(); 
			Umx_Smx.put(words.get(0).split(" ")[0], words.size() ); 
			

			//standard output=>  word count
			for (Entry<String, Integer> entry : Umx_Smx.entrySet()) {
				String key = entry.getKey().toString();
				Integer value = entry.getValue();
				//final output of total count
				System.out.println( key + "  " + value);
			}

			//will overwrite file and continue the file => to think about it
			//écrit les Sx 
			if (words.size() > 0 && args[2] != null) {
				Files.write(Paths.get(args[2]), words, Charset.defaultCharset(), StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING);
			}
			
			//write file SMX			
//			File file =  new File("SMfinal.txt");
//			FileWriter fw = new FileWriter(file,true);
//			BufferedWriter bw = new BufferedWriter(fw);
//			for (int i1 = 0; i1 < words.size(); i1++){ 
//				bw.write(words.get(i1));
//				bw.write("\n");
//			} 
//			bw.close();		
			
			//END write file SMX 
			


			//write file RMX
			String title_rmx = args[2].substring(1, args[2].length()); 
				
			FileOutputStream outputStream;
			outputStream = new FileOutputStream("R"+title_rmx+".txt"); 
			PrintWriter writer= new PrintWriter(outputStream);
			for (Entry<String, Integer> entry : Umx_Smx.entrySet()){
				String key = entry.getKey().toString();
				Integer value = entry.getValue();
				//System.out.println(Map_Umx.get(i));
				writer.write(key + "  " + value);
				writer.write("\n");
			} 
			writer.close();
			 
			
			/*generate concatenate file=> does not work....*/
			File file_rmx =  new File("RM_final.txt");	
			FileWriter fw_rmx = new FileWriter(file_rmx,true); // append the file
			BufferedWriter bw_rmx = new BufferedWriter(fw_rmx); 
			
			for (Entry<String, Integer> entry : Umx_Smx.entrySet()) {
				String key = entry.getKey().toString();
				Integer value = entry.getValue();
				bw_rmx.write(key.trim() + "  " + value);
				bw_rmx.write("\n"); 
			} 
			bw_rmx.close();		
			//END write file RMX

			///////////////////////////////////////////

		}

	}

}
