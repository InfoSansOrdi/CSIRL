package puzzle;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	public static final int maxval = 3;
	public static int XDIM = 3;
	public static int YDIM = 4;
	public static boolean toric = true;
	
	public static void main(String[] args) {
		printBanner();
		

		if (args.length > 0 && (new File(args[0])).exists()) {
			NineSquarePuzzle puzzle = new NineSquarePuzzle();
			String dataPath = null; // = "../data/data1.txt";
			dataPath = args[0];
			puzzle.load(args[0]);
			// Display the board
			StringBuffer[] lines = new StringBuffer[5];
			int piecePerLine = 4;
			int y = 0;
			while (y*piecePerLine < puzzle.getPieces().getCount()) {
				for (int l=0; l<lines.length;l++)
					lines[l] = new StringBuffer();
				for (int x=0; x<4;x++)
					puzzle.getPieces().at(y*piecePerLine + x).toBuffers(lines);
				for (int l=0; l<lines.length;l++)
					System.out.println(lines[l]);
				
				y++;
			}
			
			System.out.println("\nCalcul des solutions en cours...\n");

			Instrumentations.begin();
			puzzle.solve();
			Instrumentations.end();

			List<Board> solutions = puzzle.getSolutions();

			int i = 0;
			for (Board b : solutions) {
				i++;
				System.out.println(String.format("Solution %d:\n%s", (++i), b.toString()));
			}
				
			System.out.println(String.format("Temps de calcul estimé %s pour trouver %d solution(s)", Instrumentations
					.getTotalTimeStr(), solutions.size()));

			Instrumentations.printReport();
			
		} else {
			System.out.println("Searching for a generated "+XDIM+"x"+YDIM+(toric?" toric":"")+" board with "+maxval+" values.");
			System.out.println("Specify a filename on the command line to solve a given instance.");
			int tryAmount = 0;
			while (true) {
				if (tryAmount % 25 == 0 && tryAmount++ != 0) {
					System.out.println();
					System.out.print(tryAmount);
				}
				NineSquarePuzzle puzzle = new NineSquarePuzzle();
				puzzle.generate();
				
				// Display the board in a buffer
				StringBuffer initialBoard = new StringBuffer();
				StringBuffer[] lines = new StringBuffer[5];
				int y = 0;
				while (y*XDIM < puzzle.getPieces().getCount()) {
					for (int l=0; l<lines.length;l++)
						lines[l] = new StringBuffer();
					for (int x=0; x<XDIM;x++)
						puzzle.getPieces().at(y*XDIM + x).toBuffers(lines);
					for (int l=0; l<lines.length;l++)
						initialBoard.append(lines[l]+"\n");
					
					y++;
				}

				StringBuffer file = new StringBuffer();
				for (Piece p : puzzle.getPieces()) {
					file.append(p.getLabel() + " ");
					for (int side=0; side<4;side++)
						file.append(p.getValueAt(side) + " ");
					file.append("\n");
				}
				
				Instrumentations.begin();
				puzzle.solve();
				Instrumentations.end();

				List<Board> solutions = puzzle.getSolutions();

				if (solutions.size() > 0) {
					
					System.out.println("\nInitial board:\n"+initialBoard);
					
					System.out.println(String.format("Input file:\nGenerated data with %d solution(s)",solutions.size()));
					System.out.println(file.toString());
					
					int i = 0;
					for (Board b : solutions) {
						i++;
						System.out.println(String.format("Solution %d:\n%s", (++i), b.toString()));
					}
					
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

	private static void searchGenerated() {
	}
	
	private static void exit() {
		System.out.println("Good bye.");
		System.exit(0);
	}

	public static void printBanner() {
		System.out.println(LOGO);
	}

	private static final String VERSION = "20140205";
	private static final String LOGO = " __________                __      __                        __                 \n"
			+ " \\______   \\_____    ____ |  | ___/  |_____________    ____ |  | __ ___________ \n"
			+ "  |    |  _/\\__  \\ _/ ___\\|  |/ /\\   __\\_  __ \\__  \\ _/ ___\\|  |/ // __ \\_  __ \\\n"
			+ "  |    |   \\ / __ \\\\  \\___|    <  |  |  |  | \\// __ \\\\  \\___|    <\\  ___/|  | \\/\n"
			+ "  |______  /(____  /\\___  >__|_ \\ |__|  |__|  (____  /\\___  >__|_ \\\\___  >__|   \n"
			+ "         \\/      \\/     \\/     \\/                  \\/     \\/     \\/    \\/       \n"
			+ "                                                                     v." + VERSION + "\n";

}
/*
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