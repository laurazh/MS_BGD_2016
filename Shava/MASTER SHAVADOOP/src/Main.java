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
	 * 							Main
	 * 
	 * **************************************************************/	

	/**
	 * Main function will lauch the master that will call the slaves to do the task
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
		//args[0] will be the name of the file to read
		Preparation.FileCleaning( args[0]);

		//Preparation.FileCleaning( "forestier_mayotte.txt");


		//variables used
		String File_origine = "file_cleaned.txt";
		String Sx = null;
		ArrayList<String> Sxtable = new ArrayList<String>();
		List<String> liste_machines_ok = new ArrayList<String>();
		HashMap<String, String> SX_machine = new HashMap<String,  String>();
		//end variable

		//timer start
		long startTime = System.currentTimeMillis();



		/*********************************************************
		 * 
		 * SPLITTING 
		 * done by the master 
		 *
		 * the file will be split per lines
		 * each line will be stored in an array list Sxtable  => Preparation.FileSlicer()
		 * 1 line will generate 1 Sx file => CreateSx()
		 *
		 * @input input.txt
		 * @output Sx files
		 *
		 * *******************************************************/ 


		//input each line into one array list called Sxtable
		Sxtable = Preparation.FileSlicer(File_origine, Sx, Sxtable );

		//Create Sx file
		CreateSx( Sxtable); 

		/**********************************************************
		 * 
		 * MAPPING
		 * done by the slave  
		 * 
		 * Check connectivity of the machines connected to the network => TestMachine.test()
		 * Store the list of the connected machine in liste_machines_ok.txt
		 * Launch the slaves in parallel on "modeSXUMX" (one slave per Sx files created), it will create UMx file
		 * Store the standard output which will help to create the dictionnary key_machines
		 * with the dictionnary umx_machine, generate the new dictionnary key_umx
		 * 
		 * @input Sx file
		 * @output UMx file
		 *
		 * ********************************************************/


		// check connected machine
		TestMachine.test();

		//store the machines connected into an array List
		Path file = Paths.get("liste_machines_OK.txt"); 
		try {
			liste_machines_ok = (ArrayList<String>) Files.readAllLines(file,Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//if there are some machine connected the thread will be launch
		if (liste_machines_ok != null) {
			//Begin thread to generate the UMX file + hashmap SX-> machine 
			ArrayList<LaunchSlaveShavadoop> slaves = new ArrayList<LaunchSlaveShavadoop>();  
			for (int j=0;j<Sxtable.size();j++) {  
				//take a random machine out of the list of machines connected
				Random randomizer = new Random();
				String machine = liste_machines_ok.get(randomizer.nextInt(liste_machines_ok.size()));
				//launch the slave
				LaunchSlaveShavadoop slave = new LaunchSlaveShavadoop(machine,"cd workspace;java -jar SLAVE_SHAVADOOPTEST_JAR.jar modeSXUMX "+"S"+j, 20);
				//create the Sx_machine hashmap
				SX_machine.put( "S"+j,machine);
				slave.start();				
				slaves.add(slave); 
			}


			/*catch the standard output with " machine : key " */

			//store the standard output with a ByteArrayOutput stream
			//cast the standard output into a string 
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

			/*Generate key_machine hashmap*/

			// Since the programm takes random machine out of the connected machines to create UMx file, one machine can be used to create several UMx files
			// To create the key_machine hashmap <key, list of machine>, the list of machine must be unique, hence the use of Set<String>
			// So, even if the key appear several time in one line, the machine will only appear once 
			HashMap<String, Set<String>> key_machine = new HashMap<String, Set<String>>();
			//slicing the output to get the tuple (key, machine)
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


			/*Generate UMX_machine hashmap*/

			//the hashmap Sx_machine was generated previously
			//the machine receiving the Sx file will generate the UMx
			//Knowing the Sx_machine, we can deduct UMx_machine 
			HashMap<String, String> UMX_machine = new HashMap<String,String>(); 
			for (int i1 = 0; i1 < SX_machine.size(); i1++) {  
				UMX_machine.put( "UM"+i1,SX_machine.get("S"+i1)); 
			}
			System.out.println(UMX_machine);	 	

			/*Generate key_umx hashmap*/
			//Knowing key_machine and UMx_machine, we can deduct key_UMx
			HashMap<String, List<String>> key_umx =  new HashMap<String, List<String>>();

			//loop on the key - word to count
			for(Entry<String, Set<String>> entry : key_machine.entrySet()) {
				String cle = entry.getKey();  					//cle is the word to count
				Set<String> valeur = entry.getValue();  		// list of mahines
				//loop on the list of machine with the corresponding Umx
				for (Entry<String, String> key_um : UMX_machine.entrySet()){ 
					String valueumx = key_um.getValue();  	 
					//loop on the list of machine for a given key
					for (String elem : valeur){
						if( elem.equals(valueumx)){   
							//if the UMx was not on the list add it
							if(!key_umx.containsKey(cle)){
								key_umx.put(  cle, new ArrayList<String>());
							}
							key_umx.get( cle ).add(key_um.getKey()); 
						} 

					}

				}			 
			}

			System.out.println("key_umx :"+ key_umx); 


			// note : 
			//a machine can receive several different Sx
			// so UMx => 1 Machine but machine => several UMx


			/**********************************************************
			 * 
			 * SHUFFLING & REDUCE
			 * done by the slave
			 *  
			 * launch the slave in parallel to create the SMx and RMx
			 * create RMx_machine hashmap
			 * create the RM_final, the concatenation of all the RMx file  
			 *
			 * @intput UMx
			 * @output SMX, RMx, RM _final
			 * 
			 * ********************************************************/

			//we can recheck the connectivity of the machine before beginning any thread (optionnal)
			//include the code previously used to do the check


			//thread to be launched to create Sx and RMx
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
				//take a random machine out of the list of connected machine
				Random randomizer = new Random();
				String machine = liste_machines_ok.get(randomizer.nextInt(liste_machines_ok.size()));
				//LaunchSlaveShavadoop slave_shuffle = new LaunchSlaveShavadoop(machine,"cd workspace;java -jar SLAVE_SHAVADOOPTEST_JAR.jar modeUMXSMX "+cle+" SM"+nb_machine_shuff+ " "+listString , 20);
				LaunchSlaveShavadoop slave_shuffle = new LaunchSlaveShavadoop(machine,"cd workspace;java -jar SLAVE_SHAVADOOPTEST_JAR.jar modeUMXSMX "+cle+" SM"+nb_machine_shuff+ " "+listString , 20);
				RMX_machine.put( "RM"+nb_machine_shuff, machine );    
				slave_shuffle.start();				
				slaves_shuffle.add(slave_shuffle);
				nb_machine_shuff++;
			}
			System.out.println(RMX_machine); 

			//write file RM_final.txt 
			//File file_rmx_fin =  new File("/cal/homes/lazhou/workspace/RM_fin.txt");
			File file_rmx_fin =  new File("RM_final.txt");
			FileWriter fw_rmx_fin = new FileWriter(file_rmx_fin,true);
			BufferedWriter bw_rmx_fin = new BufferedWriter(fw_rmx_fin);

			for (int read=0; read< nb_machine_shuff;read++) {
				//read other file
				//String File_to_merge="/cal/homes/lazhou/workspace/RM"+read+".txt";
				//File f1 = new File("/cal/homes/lazhou/workspace/RM"+read+".txt"); 
				String File_to_merge="RM"+read+".txt";
				File f1 = new File("RM"+read+".txt"); 

				//wait the RMx file to be created before creating the RM_final
				while(!(f1.exists() && !f1.isDirectory())) { 
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



