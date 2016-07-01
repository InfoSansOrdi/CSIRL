package puzzle;

public class Board {

	public static final int DIMENSION = 4;

	private Piece[][] pieces;

	public Board() {
		this.pieces = new Piece[DIMENSION][DIMENSION];
	}

	public boolean putPieceAt(Piece piece, int x, int y) {
		int top = ((y > 0) && (this.pieces[y - 1][x] != null)) ? this.pieces[y - 1][x].getValueAt(Piece.BOTTOM)
				+ piece.getValueAt(Piece.TOP) : 0;
		int right = ((x < DIMENSION - 1) && (this.pieces[y][x + 1] != null)) ? this.pieces[y][x + 1]
				.getValueAt(Piece.LEFT)
				+ piece.getValueAt(Piece.RIGHT) : 0;
		int bottom = ((y < DIMENSION - 1) && (this.pieces[y + 1][x] != null)) ? this.pieces[y + 1][x]
				.getValueAt(Piece.TOP)
				+ piece.getValueAt(Piece.BOTTOM) : 0;
		int left = ((x > 0) && (this.pieces[y][x - 1] != null)) ? this.pieces[y][x - 1].getValueAt(Piece.RIGHT)
				+ piece.getValueAt(Piece.LEFT) : 0;

		boolean allowed = (top == 0) && (right == 0) && (left == 0) && (bottom == 0);

		if (allowed == true)
			this.pieces[y][x] = piece;

		return allowed;
	}

	public boolean appendPieceAt(Piece piece, int x, int y) {
		boolean allowed = false;

		if (x == 0 && y == 0) {
			allowed = true;
		} else if (y == 0) {
			Piece leftNeighbour = this.pieces[y][x - 1];
			allowed = (leftNeighbour.getValueAt(Piece.RIGHT) + piece.getValueAt(Piece.LEFT)) == 0;
		} else if (x == 0) {
			Piece topNeighbour = this.pieces[y - 1][x];
			allowed = (topNeighbour.getValueAt(Piece.BOTTOM) + piece.getValueAt(Piece.TOP)) == 0;
		} else {
			Piece leftNeighbour = this.pieces[y][x - 1];
			allowed = (leftNeighbour.getValueAt(Piece.RIGHT) + piece.getValueAt(Piece.LEFT)) == 0;
			Piece topNeighbour = this.pieces[y - 1][x];
			allowed &= (topNeighbour.getValueAt(Piece.BOTTOM) + piece.getValueAt(Piece.TOP)) == 0;
		}
		/* Two extra rules to get a looping board */
		if (x == Board.DIMENSION -1) {
			Piece rightNeighbour = this.pieces[y][0];
			allowed &= (rightNeighbour.getValueAt(Piece.LEFT) + piece.getValueAt(Piece.RIGHT)) == 0;
		}
		if (y == Board.DIMENSION -1) {
			Piece bottomNeighbour = this.pieces[0][x];
			allowed &= (bottomNeighbour.getValueAt(Piece.TOP) + piece.getValueAt(Piece.BOTTOM)) == 0;
		}
		
		if (allowed) {
			this.pieces[y][x] = piece;
		}

		return allowed;
	}
	
	
	private int nextX = -1;
	private int nextY = 0;
	
	
	public boolean isFull() {
		return nextX == 0 && nextY == Board.DIMENSION;
	}
	
	private void nextPosition() {
		if (nextX < Board.DIMENSION - 1) {
			nextX++;
		} else {
			nextX = 0;
			nextY++;
		}
	}
	
	private void lastPosition() {
		if (nextX > 0) {
			nextX--;
		} else {
			nextX = Board.DIMENSION - 1;
			nextY--;
		}		
	}
	
	
	public boolean appendPieceAt(Piece piece) {	
		nextPosition();
		//System.out.println("append x="+nextX+" y="+nextY);
		return appendPieceAt(piece, nextX, nextY);
	}
	
	public void removeLastPiece() {		
		this.pieces[nextY][nextX] = null;
		//System.out.println("remove x="+nextX+" y="+nextY);		
		lastPosition();		
	}


	public void removePieceAt(int x, int y) {
		this.pieces[y][x] = null;
	}

	public Piece getPieceAt(int x, int y) {
		return this.pieces[y][x];
	}

	@Override
	public Board clone() {
		Board copy = new Board();
		for (int y = 0; y < DIMENSION; y++) {
			for (int x = 0; x < DIMENSION; x++) {
				copy.pieces[y][x] = this.pieces[y][x].clone();
			}
		}
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		StringBuffer[] lines = new StringBuffer[5];
		for (int y = 0; y < DIMENSION; y++) {
			for (int l=0; l<lines.length;l++)
				lines[l] = new StringBuffer();
			for (int x = 0; x < DIMENSION; x++) 
				this.pieces[y][x].toBuffers(lines);
			for (int l=0; l<lines.length;l++)
				res.append(lines[l]+"\n");
		}
		return res.toString();
	}
}
