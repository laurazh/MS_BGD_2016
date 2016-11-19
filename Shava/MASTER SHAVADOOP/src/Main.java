import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream; 
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.nio.file.StandardOpenOption;
import java.text.Normalizer;
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.HashSet;
import java.util.List; 
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry; 
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Test;


public class Main {



	/** 
	 * 
	 * @param filename
	 * @param B
	 * @return Rm_fin.txt
	 * @throws IOException 
	 */
	//	public static  void CreateRMfn(String filename, Boolean B) {
	//		
	//		
	//		try{
	//			InputStream flux=new FileInputStream(filename);
	//			InputStreamReader lecture=new InputStreamReader(flux);
	//			BufferedReader buff=new BufferedReader(lecture);
	//			String ligne;
	//			while ((ligne=buff.readLine())!=null){ 
	//				Sx = ligne;
	//				Sxtable.add(Sx);
	//			}
	//			buff.close(); 
	//		}		
	//		catch (Exception e){
	//			System.out.println(e.toString());
	//		} 
	//		  
	//	}

	/**
	 * 
	 * @param Sxtable  array list of input file line
	 * @throws IOException
	 */
	private static  void CreateSx( ArrayList<String> Sxtable) throws IOException {
		for(int i = 0; i < Sxtable.size(); i++ ){ 
			String s= Sxtable.get(i);
			String output = s; 
			//FileWriter fw = new FileWriter("S"+i+".txt",true); 
			//File file = new File("/cal//homes//lazhou//workspace/S"+i+".txt");//ds workspace
			File file = new File("S"+i+".txt"); //dans le folder mastershava
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);  
			bw.write(output);
			bw.write("\n");
			bw.close();

		}

	}

	/****************************************************************** 
	 * 
	 * 
	 * 					Main
	 * 
	 * **************************************************************/	

	/**
	 * Main function do everything
	 * @param args  should change the code so that the targ is the text file you want to count
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 * @return a file with the word and its wordcount, the wordcount can also be seen in the standard output
	 * 
	 */



	public static void main(String[] args) throws IOException, InterruptedException {
		//call class Preparation
		//args[0] =  "forestier_mayotte.txt";
		Preparation.FileCleaning( "forestier_mayotte.txt");
		//Preparation.FileCleaning( args[0]);

		//variable
		String File_origine = "file_cleaned.txt";
		String Sx = null;
		ArrayList<String> Sxtable = new ArrayList<String>();
		List<String> liste_machines_ok = new ArrayList<String>();
		HashMap<String, String> SX_machine = new HashMap<String,  String>();
		//fin variable

		//timer start
		long startTime = System.currentTimeMillis();



		/*********************************************************
		 * 
		 * SPLITTING
		 * Slave - Sx file creation 
		 * chaque slave va faire  un traitement Mapping en parrallèle construire le dictionnaire “UMx - machines” 
		 * 
		 * *******************************************************/ 


		//input each line into one array list
		Sxtable = Preparation.FileSlicer(File_origine, Sx, Sxtable );

		//Create Sx file
		CreateSx( Sxtable); 

		/**********************************************************
		 * 
		 * MAPPING
		 * done by the slave
		 * Check connectivity of the machine connected to the network
		 * Launch the slaves
		 * Store the standard output
		 * 
		 * ********************************************************/


		// check connected machine
		TestMachine.test();

		//store the machine connected into an array List
		Path file = Paths.get("liste_machines_OK.txt"); 
		try {
			liste_machines_ok = (ArrayList<String>) Files.readAllLines(file,Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//Begin thread + hashmap SX-> machine   
		if (liste_machines_ok != null) {
			ArrayList<LaunchSlaveShavadoop> slaves = new ArrayList<LaunchSlaveShavadoop>();  
			for (int j=0;j<Sxtable.size();j++) {  
				//take a random machine out of the list of machine connected
				Random randomizer = new Random();
				String machine = liste_machines_ok.get(randomizer.nextInt(liste_machines_ok.size()));
				//launch the slave
				LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(machine,"cd workspace;java -jar SLAVE_SHAVADOOPTEST_JAR.jar modeSXUMX "+"S"+j, 20);
				SX_machine.put( "S"+j,machine);
				slave.start();				
				slaves.add(slave); 
			}

			//store the standard output throw a ByteArrayOutput stream
			//cast into a the standard output into a string 
			//slice the string to create the dictionary key_machine
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			for (LaunchSlaveShavadoop slave : slaves) {
				try {
					PrintStream old = System.out;
					System.setOut(ps);
					slave.join();
					System.out.flush();
					System.setOut(old);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				 
			} 
			//end thread


			// attention de prendre des valeur unique de machine
			//attention à ne pas avoir de duplica qd  il y a 2 fois le mots  gerer par la meme machine

			HashMap<String, Set<String>> key_machine = new HashMap<String, Set<String>>();
			for (String item: baos.toString().split("\n")) { 
				String s =item;	
				String key = s.substring(s.lastIndexOf("]") + 1).trim(); 
				String machine_name = s.substring(s.lastIndexOf("H") + 1,s.lastIndexOf("]")).trim();

				if(!key_machine.containsKey(key)){
					key_machine.put(  key, new HashSet<String>());
				}
				key_machine.get( key ).add(machine_name);		          
			}

			System.out.println(key_machine);

			/////////master a envoye faire machine_0faire S0 et UM0
			/////////hashmap UMX => machine 	
			HashMap<String, String> UMX_machine = new HashMap<String,String>(); 
			for (int i1 = 0; i1 < SX_machine.size(); i1++) {  
				UMX_machine.put( "UM"+i1,SX_machine.get("S"+i1)); 
			}
			System.out.println(UMX_machine);	 	

			/////  hashmap clés => UMX 	

			HashMap<String, List<String>> key_umx =  new HashMap<String, List<String>>();

			for(Entry<String, Set<String>> entry : key_machine.entrySet()) {
				String cle = entry.getKey();  				//mot à compter
				Set<String> valeur = entry.getValue();  	// liste des machines 
				//on parcours la liste des machines avec les Umx correspondant
				for (Entry<String, String> key_um : UMX_machine.entrySet()){ 
					String valueumx = key_um.getValue();  	
					// on parcours la liste des machines utilisé pour un mot données
					for (String elem : valeur){
						if( elem.equals(valueumx)){  
							//si la clé n'existe pas rajouté la valeur
							if(!key_umx.containsKey(cle)){
								key_umx.put(  cle, new ArrayList<String>());
							}
							key_umx.get( cle ).add(key_um.getKey()); 
						} 

					}

				}			 
			}
			System.out.println("key_umx :"+ key_umx);

			///!\attention, vu qu'une machine peut traiter plusieurs Sx UMx=>machine unqiue mais Machine=> plusieurs Umx

			/**********************************************************
			 * 
			 * SHUFFLING & REDUCE
			 * done by the slave
			 * Check connectivity of the machine connected to the network
			 * Launch the slaves
			 * Store the standard output
			 * 
			 * ********************************************************/

			///parcourir ma liste de mots uniques 

			// fusionne directement les rm ensemble
			//on reprend des machine au hasard 

			ArrayList<LaunchSlaveShavadoop> slaves_shuffle= new ArrayList<LaunchSlaveShavadoop>();


			HashMap<String, String> RMX_machine =  new HashMap<String, String>();
			//synchronized (slave.waitObject);

			int nb_machine_shuff=0;
			for(Entry<String, List<String>> entry : key_umx.entrySet()){ 
				String cle = entry.getKey();
				List<String> valeur = entry.getValue(); 
				String listString = "";
				for (String s : valeur)
				{
					listString += s + " ";
				} 
				//attention ici une clé pour un serveur 
				Random randomizer = new Random();
				String machine = liste_machines_ok.get(randomizer.nextInt(liste_machines_ok.size()));
				//LaunchSlaveShavadoop slave_shuffle = new LaunchSlaveShavadoop(machine,"cd workspace;java -jar SLAVE_SHAVADOOPTEST_JAR.jar modeUMXSMX "+cle+" SM"+nb_machine_shuff+ " "+listString , 20);
				LaunchSlaveShavadoop slave_shuffle = new LaunchSlaveShavadoop(machine,"cd workspace;java -jar SLAVE_SHAVADOOPTEST_JAR.jar modeUMXSMX "+cle+" SM"+nb_machine_shuff+ " "+listString , 20);

				RMX_machine.put( "RM"+nb_machine_shuff, machine );   


				//				LaunchSlaveShavadoop slave_shuffle = new LaunchSlaveShavadoop(liste_machines_ok_sliced_shuff.get(nb_machine_shuff),"cd workspace;java -jar SLAVE_SHAVADOOP4_JAR.jar modeUMXSMX "+cle+" SM"+nb_machine_shuff+ " "+listString , 20);
				//				RMX_machine.put( "RM"+nb_machine_shuff, liste_machines_ok_sliced_shuff.get(nb_machine_shuff));   
				slave_shuffle.start();				
				slaves_shuffle.add(slave_shuffle);
				nb_machine_shuff++;
			}
			System.out.println(RMX_machine);
			//System.out.println(nb_machine_shuff ); 
			
			//write file RM_FINALE 
			//File file_rmx_fin =  new File("/cal/homes/lazhou/workspace/RM_fin.txt");
			File file_rmx_fin =  new File("RM_fin.txt");

			FileWriter fw_rmx_fin = new FileWriter(file_rmx_fin,true);
			BufferedWriter bw_rmx_fin = new BufferedWriter(fw_rmx_fin);

			for (int read=0; read< nb_machine_shuff;read++) {
				//read other file
				String File_to_merge="/cal/homes/lazhou/workspace/RM"+read+".txt";
				File f1 = new File("/cal/homes/lazhou/workspace/RM"+read+".txt"); 
				//String File_to_merge="RM"+read+".txt";
				//File f1 = new File("RM"+read+".txt"); 

				while(!(f1.exists() && !f1.isDirectory())) {
					// Statements
					Thread.sleep(10);
				}
				try{
					InputStream flux=new FileInputStream(File_to_merge);
					InputStreamReader lecture=new InputStreamReader(flux);
					BufferedReader buff=new BufferedReader(lecture);
					String ligne;
					while ((ligne=buff.readLine())!=null){  
						bw_rmx_fin.write(ligne);
						bw_rmx_fin.write("\n"); 
					}
					buff.close(); 
				}		
				catch (Exception e){
					System.out.println(e.toString());
				} 				 
			} 
			bw_rmx_fin.close();		
			 
			//System.out.println("tout est fini");
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(totalTime);  

		}
		else{
			System.out.println("aucune machine connecté");
		}
		System.out.println("tout est fini");

	}

}



