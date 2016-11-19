
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;  
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


public class map_split {

	public static void main(String[]  args) throws IOException {  
		//arg[0] mode modeUMXSMX ou modeSXUMX
		//if modeSXUMX => arg[1] le fichier en entrée
		
		//System.out.println(args[0]);
		//String input_value = "Deer Deer River";
		if (args[0].equals("modeSXUMX")){
		
		String input_value = null;
		try{ 
			InputStream flux=new FileInputStream(args[1]+".txt"); 
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;
			while ((ligne=buff.readLine())!=null){ 
				input_value=ligne;
			}
			buff.close(); 
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
			//System.out.println(words[i]+ " 1");
		}
		 
		String title = args[1].substring(1, args[1].length()); 
		
		File file =  new File("UM"+ title+".txt");
		FileWriter fw = new FileWriter(file,true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for (int i = 0; i < Map_Umx.size(); i++){
			//System.out.println(Map_Umx.get(i));
			bw.write(Map_Umx.get(i));
			bw.write("\n");
		} 
		bw.close();
		
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
		//if modeSXUMX => arg[1] car  le truc à lire
		//if modeSXUMX => arg[2] SM1 
		//if modeSXUMX => arg[3] et arg[4] fichier um à lire UM1 UM2
		List<String> words = new ArrayList <String>();
		
		if (args[0].equals("modeUMXSMX")){
			//String cle = args[1];
			String input_value = null;
			//System.out.println(args.length);
			try{ 
				//InputStream flux=new FileInputStream("UMx.txt"); 
				for (int i=3;i < args.length; i++){
				//int i=3;
				InputStream flux=new FileInputStream(args[i]+".txt"); 
				InputStreamReader lecture=new InputStreamReader(flux);
				BufferedReader buff=new BufferedReader(lecture);
				String ligne;				
				//System.out.println(args[i]);
				while ((ligne=buff.readLine())!=null){ 
					input_value=ligne;//lit chaque ligne
					//System.out.println(input_value);
					//System.out.println(input_value.split(" ")[0].toUpperCase()+" "+args[1].toUpperCase());	 
					if(input_value.split(" ")[0].toUpperCase().equals( args[1].toUpperCase())){ 				
						words.add(input_value); 
					}
				}
				buff.close(); 
				}
				}		
				catch (Exception e){
				System.out.println(e.toString());
				}
			 
//			ArrayList<String> Umx_Smx = new ArrayList<String>();  
			HashMap<String,Integer> Umx_Smx = new HashMap<String,Integer>(); 
			Umx_Smx.put(words.get(0).split(" ")[0], words.size() ); 
			 
			//standard output
			for (Entry<String, Integer> entry : Umx_Smx.entrySet()) {
			    String key = entry.getKey().toString();
			    Integer value = entry.getValue();
			    System.out.println( key + "  " + value);
			}
			
			//will overwrite file and continue the file => to think about it
			//write file SMX
			File file =  new File(args[2]+".txt");
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int i1 = 0; i1 < words.size(); i1++){ 
				bw.write(words.get(i1));
				bw.write("\n");
			} 
			bw.close();		
			//END write file SMX 
			
			//write file RMX
			String title_rmx = args[2].substring(1, args[2].length()); 
			File file_rmx =  new File("R"+title_rmx+".txt");
			//System.out.println("R"+title_rmx+".txt");
			FileWriter fw_rmx = new FileWriter(file_rmx,true);
			BufferedWriter bw_rmx = new BufferedWriter(fw_rmx);
			
			for (Entry<String, Integer> entry : Umx_Smx.entrySet()) {
			    String key = entry.getKey().toString();
			    Integer value = entry.getValue();
			    bw_rmx.write(key + "  " + value);
			    bw_rmx.write("\n"); 
			} 
			bw_rmx.close();		
			//END write file RMX
		
		///////////////////////////////////////////
		
	}
	
	}
}
