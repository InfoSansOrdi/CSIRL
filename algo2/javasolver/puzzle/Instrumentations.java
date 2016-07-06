package puzzle;

public class Instrumentations {

	public static int recursiveCallCount = 0;

	private static long startTime = 0;
	private static long endTime = 0;
	public static int piecesTriedCount = 0;
	public static int piecesSuccessfullyTriedCount = 0;
	public static int nbSolutions = 0;
	
	public static void reset() {
		piecesTriedCount = 0;
		piecesSuccessfullyTriedCount = 0;
		nbSolutions = 0;
	}

	public static void begin() {
		startTime = System.currentTimeMillis();
	}

	public static void end() {
		endTime = System.currentTimeMillis();
	}

	public static String getTotalTimeStr() {
		return formatTime(endTime - startTime);
	}

	public static void printReport() {
		System.out.println();
		System.out.println("-- Statistics");
		
		System.out.println(String.format("Recursive calls = %,d", recursiveCallCount));
		System.out.println(String.format("Pieces tried = %,d", piecesTriedCount));
		System.out.println(String.format("Pieces tried and placed = %,d", piecesSuccessfullyTriedCount));
	}
	
	public static String formatTime(long mstime) {
		long tmp = mstime;
		long hours = tmp / (1000 * 60 * 60);
		tmp = tmp % (1000 * 60 * 60);
		long minutes = tmp / (1000 * 60);
		tmp = tmp % (1000 * 60);
		long seconds = tmp / 1000;
		tmp = tmp % 1000;
		long millisecs = tmp;

		return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millisecs);
	}

}
