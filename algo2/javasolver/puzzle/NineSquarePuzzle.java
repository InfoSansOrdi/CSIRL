package puzzle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NineSquarePuzzle {

	private String title;
	private Pool pieces;
	private Board board;
	private List<Board> solutions;

	public NineSquarePuzzle() {
		this.solutions = new ArrayList<Board>();
		this.pieces = new Pool(0);
		this.board = new Board();
	}

	public String getTitle() {
		return this.title;
	}

	public Pool getPieces() {
		return this.pieces;
	}

	public List<Board> getSolutions() {
		return this.solutions;
	}

	public boolean isPerfect() {
		return this.pieces.isPerfect();
	}

	public void solve() {
		pieces.shuffle();
		solve(0, 0);
		//solveR();
	}

	private boolean solveR() {
		Instrumentations.recursiveCallCount++;
		for (int index=0; index<this.pieces.getCount(); index++) {
			if (this.pieces.isPieceAvailableAt(index)) {
				Piece current = this.pieces.takePieceAt(index);
				for (int rotation = 0; rotation < 4; rotation++) {
					Instrumentations.piecesTriedCount++;
			
					if (this.board.appendPieceAt(current)) {
						Instrumentations.piecesSuccessfullyTriedCount++;
						if (this.board.isFull()) {
							this.solutions.add(this.board.clone());
							return true;
						} else {
							solveR();
						}
					}
					this.board.removeLastPiece();
					current.rotateClockwise();	
				}
				this.pieces.releasePieceAt(index);
			}
		}
		return true;	
	}


	private boolean solve(int x, int y) {
		Instrumentations.recursiveCallCount++;
		
		if (x == 0 && y == Main.YDIM) {
			this.solutions.add(this.board.clone());
			return true;
		}
		//System.out.print(x+","+y+" ");

		int index = 0;
		Piece current = null;
		while (index < this.pieces.getCount() 
				&& (x!=0||y!=0||index==0) // Lock the upper left piece to be the first of the pool (to kill symmetries)
			  ) {
			if (this.pieces.isPieceAvailableAt(index)) {
				current = this.pieces.takePieceAt(index);
				// Upper left piece cannot rotate (to kill symmetries)
				for (int rotation = 0; rotation < (x==0 && y==0 ? 1 : 4); rotation++) {
					Instrumentations.piecesTriedCount++;
					// if (this.board.putPieceAt(current, x, y)) {
					if (this.board.appendPieceAt(current, x, y)) {
						Instrumentations.piecesSuccessfullyTriedCount++;
						if (x == Main.XDIM - 1) {
							solve(0, y + 1);
						} else {
							solve(x + 1, y);
						}
						this.board.removePieceAt(x, y);
					}
					current.rotateClockwise();
				}
				this.pieces.releasePieceAt(index);
			}
			index++;
		}

		return true;
	}

	public void generate() {
		// maxocc: amount of each symbol that we want = #sides * #cells / #symbols
		double maxoccD = 4.0 * Main.XDIM * Main.YDIM / (Main.maxval*2);
		int maxocc = (int)maxoccD;
		if (maxoccD != (double)maxocc) {
			System.err.println("Max occurence of each symbol is not an integer: "+maxoccD);
			maxocc+=1;
		}
		Random r = new Random(System.currentTimeMillis());
		
		this.title  = "generated";
		this.pieces = new Pool(Main.XDIM * Main.YDIM);
		
		char[] names = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		int[] occ = new int[2*Main.maxval+1];
		for (int rank=0; rank < Main.XDIM * Main.YDIM; rank++) {
			
			Piece p = new Piece(names[rank]);
			for (int side=0; side<4; side++) {
				int val = 0;
				int count=0;
				while (val == 0 // Don't produce 0
						|| occ[val+Main.maxval] >= maxocc) {// Dont produce more of a given value than expected
					val = r.nextInt(2*Main.maxval+1) - Main.maxval;
					if (count++ == 1000) {
						System.err.println("Cannot produce a new side value. Maxocc="+maxocc);
						for (int j=0;j<2*Main.maxval+1;j++)
							System.out.println("#"+(j-Main.maxval)+" -> "+occ[j]);
						System.exit(1);						
					}
				}
				occ[val + Main.maxval]++;
				
				p.setValueAt(side, val);
			}
			pieces.addPiece(p);
		}
		for (int i=0;i<2*Main.maxval+1;i++)
			if (i-Main.maxval != 0 && occ[i]!=maxocc) {
				System.err.println("I have "+occ[i]+" of "+(i-Main.maxval)+" instead of "+maxocc);
				for (int j=0;j<2*Main.maxval+1;j++)
					System.out.println("#"+(j-Main.maxval)+" -> "+occ[j]);
				System.exit(1);
			}
		System.out.print("g");
	}
	
	public void load(String path) {
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(new File(path))));
			this.title = reader.readLine();
			this.pieces = Pool.load(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
