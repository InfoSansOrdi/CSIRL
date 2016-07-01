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
		
		if (x == 0 && y == Board.DIMENSION) {
			this.solutions.add(this.board.clone());
			return true;
		}

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
						if (x == Board.DIMENSION - 1) {
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

	public void generate(int size, int maxval, int maxocc) {
		Random r = new Random(System.currentTimeMillis());
		
		this.title  = "generated";
		this.pieces = new Pool(size*size);
		
		char[] names = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		int[] occ = new int[2*maxval+1];
		for (int rank=0; rank < size*size; rank++) {
			
			Piece p = new Piece(names[rank]);
			for (int side=0; side<4; side++) {
				int val = 0;
				while (val == 0 // Don't produce 0
						|| occ[val+maxval] >= maxocc) // Dont produce more of a given value than expected
					val = r.nextInt(2*maxval+1) - maxval;
				occ[val + maxval]++;
				
				p.setValueAt(side, val);
			}
			pieces.addPiece(p);
			/*
			System.out.println(p.toString());
			for (int i=0;i<2*maxval+1;i++)
				System.out.println("#"+(i-maxval)+"->"+occ[i]);
			 */
		}
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
