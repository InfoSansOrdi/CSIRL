package puzzle;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Main {
	
	public static final int maxval = 3;
	public static int XDIM = 6;
	public static int YDIM = 4;
	public static boolean toric = true;
	public static boolean signed = true;
	
	public static void main(String[] args) {
		Random rnd = new Random(System.currentTimeMillis());
		printBanner();

		String dataPath = args[0];
		if (args.length > 0 && (new File(dataPath)).exists()) {
			System.out.println("Searching for a "+XDIM+"x"+YDIM+(toric?" toric":" non-toric")+" solution with "+maxval+(signed?" signed":" non-signed")+" values using the pieces of "+dataPath+".");
			NineSquarePuzzle puzzle = new NineSquarePuzzle();
			puzzle.load(dataPath);
		
			if (puzzle.getPieces().getCount() != XDIM*YDIM) {
				System.err.println("Got "+puzzle.getPieces().getCount()+" pieces for a "+XDIM+"x"+YDIM+" board. Bailing out");
				System.exit(1);
			}
			// Display the board
			StringBuffer[] lines = new StringBuffer[5];
			for (int y=0;y<YDIM;y++) {
				for (int l=0; l<lines.length;l++)
					lines[l] = new StringBuffer();
				for (int x=0; x<XDIM;x++)
					puzzle.getPieces().at(y*XDIM + x).toBuffers(lines);
				for (int l=0; l<lines.length;l++)
					System.out.println(lines[l]);
			}
			
			// Check if we must set a sign to the values
			boolean boardSigned = false;
			// Search for negative values 
			for (Piece p : puzzle.getPieces()) 
				for (int side = 0; side<4; side++)
					boardSigned |= (p.getValueAt(side) < 0);

			if (signed && !boardSigned) {
				System.out.println("Recomputing signed values for the board.");
			} if (!signed && boardSigned) {
				System.out.println("Your dataset is signed, bailing out.");
				exit();
			}
			
			System.out.println("\nComputing solutions...\n");
			Instrumentations.begin();
			
			// do only once if we are not requested for a signed board, or if the board is already signed
			boolean done = false;
			while (!done) {
				// Randomly flip the sign of the values, ensuring that the sum of all values is 0 (if not, the board is probably impossible)
				if (signed != boardSigned) {
					int sum=42;
					while (sum != 0) {
						System.out.print(".");
						sum=0;
						for (Piece p : puzzle.getPieces()) 
							for (int side = 0; side<4; side++) {
								if (rnd.nextBoolean())
									p.flipSign(side);
								sum += p.getValueAt(side);
							}
					}
					System.out.print("g");
				}
				
				puzzle.solve();
				
				// Recompute a new board if no solution found (or too much solutions) and more than one loop requested
				if (signed == boardSigned) {
					done = true;
				} else {
					if (puzzle.getSolutions().size() > 16) {
						puzzle.reset();
						System.out.println("Too much solutions. Let's take another try");
					}
					if (! puzzle.getSolutions().isEmpty())
						done = true;
				}
			}
			Instrumentations.end();

			List<Board> solutions = puzzle.getSolutions();

			int i = 0;
			for (Board b : solutions) {
				if (i<25)
					System.out.println(String.format("Solution %d:\n%s", ++i, b.toString()));
			}
			if (i>=25)
				System.out.println("(more results omitted)\n\n");
				
			System.out.println(String.format("Temps de calcul estimé %s pour trouver %d solution(s)", Instrumentations
					.getTotalTimeStr(), solutions.size()));

			Instrumentations.printReport();
			
		} else {
			System.out.println("Searching for a generated "+XDIM+"x"+YDIM+(toric?" toric":" non-toric")+" board with "+maxval+(signed?" signed":" non-signed")+" values.");
			System.out.println("Specify a filename on the command line to solve a given instance.");
			int tryAmount = 0;
			Instrumentations.begin();
			while (true) {
				if (++tryAmount % 50 == 0) {
					System.out.println();
					System.out.print(tryAmount+" tries so far. ");
				}
				NineSquarePuzzle puzzle = new NineSquarePuzzle();
				puzzle.generate();
				
				// Display the board in a buffer
				StringBuffer initialBoard = new StringBuffer();
				StringBuffer[] lines = new StringBuffer[5];
				for (int y=0;y<YDIM;y++) {
					for (int l=0; l<lines.length;l++)
						lines[l] = new StringBuffer();
					for (int x=0; x<XDIM;x++)
						puzzle.getPieces().at(y*XDIM + x).toBuffers(lines);
					for (int l=0; l<lines.length;l++)
						initialBoard.append(lines[l]+"\n");
				}

				StringBuffer file = new StringBuffer();
				for (Piece p : puzzle.getPieces()) {
					file.append(p.getLabel() + " ");
					for (int side=0; side<4;side++)
						file.append(p.getValueAt(side) + " ");
					file.append("\n");
				}
				
				puzzle.solve();

				List<Board> solutions = puzzle.getSolutions();

				if (solutions.size() > 0) {
					Instrumentations.end();
					
					System.out.println("\nInitial board:\n"+initialBoard);
					
					System.out.println(String.format("Input file:\nGenerated data with %d solution(s)",solutions.size()));
					System.out.println(file.toString());
					
					int i = 0;
					for (Board b : solutions) {
						if (i<25)
							System.out.println(String.format("Solution %d:\n%s", ++i, b.toString()));
					}
					if (i>=25)
						System.out.println("(more results omitted)\n\n");
					
					System.out.println(String.format("Temps de calcul estimé %s pour trouver %d solution(s)", Instrumentations
							.getTotalTimeStr(), solutions.size()));

					Instrumentations.printReport();

					
					exit();
				} else {
					System.out.print(".");
				}
			}			
		}

	}

	private static void exit() {
		System.out.println("Good bye.");
		System.exit(0);
	}

	public static void printBanner() {
		System.out.println(LOGO);
	}

	private static final String VERSION = "20160703";
	private static final String LOGO = " __________                __      __                        __                 \n"
			+ " \\______   \\_____    ____ |  | ___/  |_____________    ____ |  | __ ___________ \n"
			+ "  |    |  _/\\__  \\ _/ ___\\|  |/ /\\   __\\_  __ \\__  \\ _/ ___\\|  |/ // __ \\_  __ \\\n"
			+ "  |    |   \\ / __ \\\\  \\___|    <  |  |  |  | \\// __ \\\\  \\___|    <\\  ___/|  | \\/\n"
			+ "  |______  /(____  /\\___  >__|_ \\ |__|  |__|  (____  /\\___  >__|_ \\\\___  >__|   \n"
			+ "         \\/      \\/     \\/     \\/                  \\/     \\/     \\/    \\/       \n"
			+ "                                                                     v." + VERSION + "\n";

}
/* Toric Signed Mac Mahon, 168 solutions
+----------++----------++----------++----------++----------++----------+
|     1    ||     3    ||     2    ||    -1    ||     1    ||    -1    |
| 2 [V0]  3||-3 [O1] -3|| 3 [U0]  3||-3 [D0]  2||-2 [M3]  2||-2 [E2] -2|
|     2    ||     2    ||    -2    ||     3    ||     2    ||     3    |
+----------++----------++----------++----------++----------++----------+
+----------++----------++----------++----------++----------++----------+
|    -2    ||    -2    ||     2    ||    -3    ||    -2    ||    -3    |
| 3 [K0]  3||-3 [I3] -2|| 2 [C1]  3||-3 [F1]  3||-3 [R1]  1||-1 [N2] -3|
|     1    ||    -3    ||     2    ||     3    ||    -1    ||    -3    |
+----------++----------++----------++----------++----------++----------+
+----------++----------++----------++----------++----------++----------+
|    -1    ||     3    ||    -2    ||    -3    ||     1    ||     3    |
| 3 [T2]  3||-3 [H0]  1||-1 [S3]  1||-1 [W1] -1|| 1 [Q3] -1|| 1 [J2] -3|
|    -1    ||    -1    ||     2    ||    -2    ||    -1    ||     2    |
+----------++----------++----------++----------++----------++----------+
+----------++----------++----------++----------++----------++----------+
|     1    ||     1    ||    -2    ||     2    ||     1    ||    -2    |
| 2 [A3]  1||-1 [X3] -2|| 2 [L0] -2|| 2 [P1] -3|| 3 [B3]  1||-1 [G1] -2|
|    -1    ||    -3    ||    -2    ||     1    ||    -1    ||     1    |
+----------++----------++----------++----------++----------++----------+


+----------++----------++----------++----------+
|     3    ||    -1    ||     4    ||     3    |
|-1 [P0]  4||-4 [G3] -2|| 2 [O1] -2|| 2 [L0]  1|
|     2    ||     2    ||    -1    ||    -2    |
+----------++----------++----------++----------+
+----------++----------++----------++----------+
|    -2    ||    -2    ||     1    ||     2    |
| 4 [I0]  3||-3 [K0]  4||-4 [B3]  3||-3 [C1] -4|
|    -3    ||     1    ||    -3    ||    -4    |
+----------++----------++----------++----------+
+----------++----------++----------++----------+
|     3    ||    -1    ||     3    ||     4    |
| 4 [F1] -2|| 2 [E3] -1|| 1 [M0]  3||-3 [A1] -4|
|    -4    ||     2    ||    -1    ||     1    |
+----------++----------++----------++----------+
+----------++----------++----------++----------+
|     4    ||    -2    ||     1    ||    -1    |
| 1 [D2]  3||-3 [J0]  4||-4 [H2]  2||-2 [N1] -1|
|    -3    ||     1    ||    -4    ||    -3    |
+----------++----------++----------++----------+
*/