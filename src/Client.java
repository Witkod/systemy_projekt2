import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Klasa służąca do uruchomienia klienta.
 * 
 * @author Robert Gaca i Tomasz Giereś
 * 
 */

public class Client {

	/**
	 * Główna metoda
	 * 
	 * @param argv
	 */
	public static void main(String[] argv) {
		try {

			log("Localhost ip address: " + java.net.InetAddress.getLocalHost());

// wczytanie tablicy D[][] z pliku 
			TrafficCircleSimulationManager trafficCircleSimulationManager = new TrafficCircleSimulationManager();
			float[][] chances = trafficCircleSimulationManager
					.load_data("params.txt");
// inicjalizacja serwerów
			List<TrafficCircleSimulationInterface> servers = Collections
					.synchronizedList(new ArrayList<TrafficCircleSimulationInterface>());
// pobiera info o dostępnych serwerach
			String[][] machines = Client.load_servers("machines.txt");
// łączy sie z serwerami
			for (String[] lookup_params : machines) {
				String lookup_param = "//" + lookup_params[0] + "/"
						+ lookup_params[1];
// uzyskanie referencji do namiastki obiektu zdalnego od rejestru
				servers.add((TrafficCircleSimulationInterface) Naming
						.lookup(lookup_param));
				log("Podłączony klient: " + lookup_params[1]);
			}
// uruchamia obliczenia na każdy z serwerów
			for (TrafficCircleSimulationInterface server : servers) {
				server.set_chances(chances); // każdy serwer wykonuje własną symulację ronda
				server.start(); // uruchomienie obliczeń na każdym serwerze
			}

			int[][] total_results = new int[3][4];

			int results_cnt = 0;
// dopóki nie zbierze od wszystkich serwerów danych to zbiera
			while (results_cnt < machines.length) {
//
				for (Iterator<TrafficCircleSimulationInterface> it = servers
						.iterator(); it.hasNext();) {
					TrafficCircleSimulationInterface server = (TrafficCircleSimulationInterface) it
							.next();
// jesli dany serwer jest gotowy i nie wysyłał jeszcze rezultatów to pobiera dane
					if (server.is_done() && !server.sent_results()) {
						results_cnt++;

						int[][] res = server.get_results();
						log("Gotowy serwer nr: " + results_cnt);

						log("Wypisuje wyniki.");
						for (int[] tab_res : res) {
							String line = "";
							for (int num : tab_res) {
								line = line + num + "  ";
							}
							log(line);
						}

						// Sumujemy
						for (int i = 0; i < total_results.length; ++i) {
							for (int j = 0; j < total_results[i].length; ++j) {
								total_results[i][j] += res[i][j];
							}
						}

						// log("Suma dotychczas: ");
						// for (int[] tab_res : total_results) {
						// String line = "";
						// for (int num : tab_res) {
						// line = line + num + "  ";
						// }
						// log(line);
						// }
					}
				}
			}
// suma i wypis średnich wyników ze wszystkich maszyn
			log("---");
			log("\n +--- Wyniki po 3200 iteracji na kazdej z "
					+ machines.length + " maszyn:\n");
			System.out
					.printf("| %1s | %10s %10s %10s %10s %10s\n", "",
							"Nadjechalo", "Czekalo", "Kolejka", "Sr. kol.",
							"% Czekalo");
			for (int i = 0; i <= 3; ++i) {
				final float AVG_QUEUE = (float) total_results[2][i]
						/ (float) 32000 / (float) 4;
				final float WAITING_PERC = 100.0f * (float) total_results[1][i]
						/ (float) total_results[0][i];

				System.out.printf("| %d | %10d %10d %10d %10.2f %10.2f\n", i,
						total_results[0][i], total_results[1][i],
						total_results[2][i], AVG_QUEUE, WAITING_PERC);

			}

		} catch (Exception e) {
			System.out.println("HelloClient exception: " + e);
		}
	}

	private static void log(String msg) {
		System.out.println(((DateFormat) new SimpleDateFormat("HH:mm:ss"))
				.format(new Date()) + "| " + msg);
	}

// wczytuje serwery z pliku machines.txt
//
	private static String[][] load_servers(String filename) {
		String[][] array = null;
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader fp = new BufferedReader(fr);

			List<String> ip_addresses = new ArrayList<String>();
			List<String> names = new ArrayList<String>();

			String line;
			while ((line = fp.readLine()) != null) {
				String[] words = line.split(" ");
				ip_addresses.add(words[0]);
				names.add(words[1]);
			}

			array = new String[ip_addresses.size()][2];
			for (int cnt = 0; cnt < ip_addresses.size(); ++cnt) {
				array[cnt][0] = ip_addresses.get(cnt);
				array[cnt][1] = names.get(cnt);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return array;
	}
}
