package puzzle;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class Pool implements Iterable<Piece> {

	private Piece[] pieces;
	private boolean[] available;
	private int count;

	public Pool(int size) {
		this.count = 0;
		this.pieces = new Piece[size];
		this.available = new boolean[size];
	}

	public Iterator<Piece> iterator() {
		return new ArrayIterator(this.pieces);
	}
	
	public int getCount() {
		return this.count;
	}
	
	public Piece at(int index) {
		return this.pieces[index];		
	}

	public Piece takePieceAt(int index) {
		this.available[index] = false;
		return this.pieces[index];
	}

	public void releasePieceAt(int index) {
		this.available[index] = true;
	}

	public void addPiece(Piece piece) {
		this.available[count] = true;
		this.pieces[count] = piece;
		this.count++;
	}

	public boolean isPieceAvailableAt(int index) {
		return this.available[index];
	}

     	/** True if all pieces are perfect, ie if they contain 1, 2, 3 and 4 each, with two positives and two negatives */
	public boolean isPerfect() {
		for (Piece p : this.pieces) {
			if (!p.isPerfect())
				return false;
		}
		return true;
	}
	
	public void shuffle() { //  Durstenfeld shuffle (?)
	    Random rnd = new Random(System.currentTimeMillis());
	    for (int i = pieces.length - 1; i > 0; i--) {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      Piece p = pieces[index];
	      pieces[index] = pieces[i];
	      pieces[i] = p;
	    }
	}
	
	public static Pool load(LineNumberReader r) throws IOException {
		List<Piece> loadedPieces = new ArrayList<Piece>();
		String line;
		while ((line = r.readLine()) != null) {
			Scanner s = new Scanner(line);
			if (s.findInLine("(\\S)\\s+(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)") != null) {
				MatchResult result = s.match();
				Piece piece = new Piece(result.group(1).charAt(0));
				int direction = Piece.TOP;
				for (int i = 2; i <= result.groupCount(); i++) {
					piece.setValueAt(direction, Integer.parseInt(result.group(i)));
					direction++;
				}
				loadedPieces.add(piece);
			}
		}

		Pool result = new Pool(loadedPieces.size());
		for (Piece p : loadedPieces) {
			result.addPiece(p);
		}
		return result;
	}

}
