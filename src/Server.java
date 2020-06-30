import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasa służąca do uruchomienia serwera.
 * 
 * @author tomek
 * 
 */
class Server {
	/**
	 * Uruchamia
	 * 
	 * @param argv
	 *            nazwa pod którą serwer zarejestruje obiekt symulacji w
	 *            rejestrze.
	 */
	public static void main(String[] argv) {
		try {
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new RMISecurityManager());
			}
			log("Server is ready.");
			Naming.rebind(argv[0], new TrafficCircleSimulation(argv[0]));
			log("Zarejestrowany jako: " + argv[0]);
		} catch (Exception e) {
			log("Server failed: " + e);
		}
	}

	private static void log(String msg) {
		System.out.println(((DateFormat) new SimpleDateFormat("HH:mm:ss"))
				.format(new Date()) + "| " + msg);
	}
}