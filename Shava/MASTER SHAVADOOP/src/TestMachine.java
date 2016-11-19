
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestMachine {
	public static void test() {

		List<String> machines;
		ArrayList<TestConnectionSSH> listeTests = new ArrayList<TestConnectionSSH>();

		Path filein = Paths.get("liste_machines.txt");
		try {
			machines = Files.readAllLines(filein, Charset.forName("UTF-8"));
			for (String machine : machines) {
				/*
				 * on teste la connection SSH pendant 7 secondes maximum
				 */
				TestConnectionSSH test = new TestConnectionSSH(machine, 7);
				test.start();
				listeTests.add(test);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ArrayList<String> liste_machines_ok = new ArrayList<String>();

		for (TestConnectionSSH test : listeTests) {
			try {
				test.join();// on attend la fin du test
				if (test.isConnectionOK()) {
					liste_machines_ok.add(test.getMachine());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		Path file = Paths.get("liste_machines_OK.txt");
		try {
			Files.write(file, liste_machines_ok, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}
