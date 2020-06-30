import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrafficCircleSimulation extends UnicastRemoteObject implements
		TrafficCircleSimulationInterface {
			///////?????????????????
	private static final long serialVersionUID = 7896795898928782846L;

	private enum Entrance {
		N, W, S, E
	};

	private final static int CIRCLE_SIZE = 16;
	private final static int EXITS_NUMBER = 4;

	private final int REQUESTED_ITERATIONS = 33000;
	private final int offset[] = { 0, 4, 8, 12 };
	private final int f[] = { 3, 3, 4, 2 }; // Original: {3, 3, 4, 2};

	private float CHANCES[][] = { /* Original: */
	{ 0.1f, 0.2f, 0.5f, 0.2f }, /* {0.1, 0.2, 0.5, 0.2}, */
	{ 0.2f, 0.1f, 0.3f, 0.4f }, /* {0.2, 0.1, 0.3, 0.4}, */
	{ 0.5f, 0.1f, 0.1f, 0.3f }, /* {0.5, 0.1, 0.1, 0.3}, */
	{ 0.3f, 0.4f, 0.2f, 0.1f } /* {0.3, 0.4, 0.2, 0.1}, */
	};

	private int circle[] = new int[CIRCLE_SIZE];
	private int new_circle[] = new int[CIRCLE_SIZE];

	private int arrival[] = new int[EXITS_NUMBER];
	private int arrival_cnt[] = new int[EXITS_NUMBER];
	private int wait_cnt[] = new int[EXITS_NUMBER];
	private int queue[] = new int[EXITS_NUMBER];
	private int queue_accum[] = new int[EXITS_NUMBER];
	/*
	 * private int total_arrival_cnt[] = new int[EXITS_NUMBER]; private int
	 * total_wait_cnt[] = new int[EXITS_NUMBER]; private int total_queue_accum[]
	 * = new int[EXITS_NUMBER];
	 */
	//
	boolean _isDone = false; //flaga oznaczająca, że zakonczono symulacje
	boolean _sentResults = false; //flaga oznaczająca, że wyniki zostały wysłane

	private void log(String msg) {
		System.out.println(((DateFormat) new SimpleDateFormat("HH:mm:ss"))
				.format(new Date()) + "| " + msg);
	}

	public void start() {
		log("start()");

		int i = 0;
		int iteration = 0;

		for (i = 0; i <= 15; ++i) {
			circle[i] = -1;
		}

		for (iteration = 0; iteration <= REQUESTED_ITERATIONS; iteration++) {
			// int k;

			// Przyjeżdżają nowe samochody
			for (i = 0; i <= 3; ++i) {
				final double u = Math.random();
				final double prob = 1.0f / f[i];
				if (u <= prob) {
					arrival[i] = 1;
					arrival_cnt[i]++;
				} else {
					arrival[i] = 0;
				}
			}

			// Nastepuje iteracja, ruch na rondzie-----\n
			for (i = 0; i <= 15; ++i) {
				final int j = (i + 1) % 16;
				if ((circle[i] == -1) || (circle[i] == j)) {
					new_circle[j] = -1;
				} else {
					new_circle[j] = circle[i];
				}
			}

			for (i = 0; i <= 15; ++i)
				circle[i] = new_circle[i];

			// Samochody wjeżdżają na rondo
			for (i = 0; i <= 3; ++i) {
				if (circle[offset[i]] == -1) {
					// Jest miejsce na wjazd samochodu
					if (queue[i] > 0) {
						// Czekający samochod wjezdza
						queue[i]--;
						circle[offset[i]] = choose_exit(Entrance.values()[i]);
					} else if (arrival[i] > 0) {
						// Samochod ktory nadjechal wjeżdża na rondo
						arrival[i] = 0;
						circle[offset[i]] = choose_exit(Entrance.values()[i]);
					}
				} else {
					// Nic sie nie dzieje...
				}

				if (arrival[i] > 0) {
					// Samochód, który właśnie nadjechał ląduje w kolejce
					wait_cnt[i]++;
					queue[i]++;
				}
			}
			for (i = 0; i <= 3; ++i) {
				queue_accum[i] += queue[i];
			}

		} // koniec głównej pętli
		/*** Koniec algorytmu ***/

		_isDone = true;
	}

	int choose_exit(Entrance entrance_number) {
		float dist_n, dist_w, dist_s;
		Entrance exit_no;

		final float RANDOM = (float) Math.random();

		dist_n = CHANCES[entrance_number.ordinal()][0];
		dist_w = dist_n + CHANCES[entrance_number.ordinal()][1];
		dist_s = dist_w + CHANCES[entrance_number.ordinal()][2];

		if (RANDOM < dist_n)
			exit_no = Entrance.N;
		else if (dist_n <= RANDOM && RANDOM < dist_w)
			exit_no = Entrance.W;
		else if (dist_w <= RANDOM && RANDOM < dist_s)
			exit_no = Entrance.S;
		else
			exit_no = Entrance.E;

		return offset[exit_no.ordinal()];
	}

	private String message;

	public TrafficCircleSimulation(String msg) throws RemoteException {
		message = msg;
	}

	public String say(String msg) throws RemoteException {
		return message + " | Argument: " + msg;
	}

	@Override
	public void set_chances(float[][] chances) throws RemoteException {
		log("set_chances()");
		CHANCES = chances;
	}

	@Override
	public boolean is_done() throws RemoteException {
		log("is_done()");
		return _isDone;
	}

	@Override
	public boolean sent_results() throws RemoteException {
		return _sentResults;
	}

	@Override
	public int[][] get_results() throws RemoteException {
		log("get_results()");
		int results[][] = new int[3][4];
		results[0] = arrival_cnt;
		results[1] = wait_cnt;
		results[2] = queue_accum;

		for (int result[] : results) {
			for (int field : result) {
				System.out.print(field + " ");
			}
			System.out.println();
		}
		log("get_results() end");

		_sentResults = true;
		return results;
	}
}
