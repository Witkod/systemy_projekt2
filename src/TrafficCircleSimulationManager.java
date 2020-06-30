import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Klasa zawierająca metody potrzebne klientowi do obsługi serwerów.
 * 
 * @author tomek
 * 
 */
public class TrafficCircleSimulationManager {
	/**
	 * Służy do odczytania z pliku tekstowego parametrów symulacji, tj. tablicy
	 * prawdopodobieństw zjazdu.
	 * 
	 * @param filename
	 *            nazwa pliku z parametrami.
	 * @return
	 */
	public float[][] load_data(String filename) {

// wczytuje tylko tablice D[][]
		float array[][] = new float[4][4];
		FileReader fr;
		try {
			fr = new FileReader(filename);

			BufferedReader fp = new BufferedReader(fr);

			int i, j;
			int line_cnt = 0;

			String line;
			while ((line = fp.readLine()) != null) {
				String[] numbers = line.split(" ");
				int numbers_cnt = 0;
				for (String number : numbers) {
					array[line_cnt][numbers_cnt] = Float.valueOf(number);
					++numbers_cnt;
				}
				++line_cnt;
			}
// sumuje prawdopodobienstawa czy nie sa > 1
			float sums[] = new float[4];
			for (i = 0; i <= 3; ++i) {
				for (j = 0; j <= 3; ++j) {
					sums[i] += array[i][j];
				}
			}
// sprawdza poprawnosc wczytanych prawdopodobienstw
			for (i = 0; i <= 3; ++i) {
				if (sums[i] != 1.0) {
					String msg = String
							.format("\n+--- Tablica prawdopodobienstw ma bledy.\n"
									+ "| Suma prawdopodobieństw dla zjazdu nr %d wynosi %f.\n"
									+ "| Popraw plik params.txt tak, żeby suma prawdopodobienstw w kazdej linii wynosila 1.",
									i, sums[i]);
					throw new RuntimeException(msg);

				}
			}
// wypisuje wczytane wartosci
			System.out.printf("\n+--- Tablica prawdopodobieństw:\n");

			for (i = 0; i <= 3; ++i) {
				System.out.printf("| %d | ", i);
				for (j = 0; j <= 3; ++j) {
					System.out.printf("%1.2f  ", array[i][j]);
				}
				System.out.printf("| Suma: %f\n", sums[i]);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
		}

		return array;
	}
}
