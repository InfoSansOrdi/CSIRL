package puzzle;

import java.util.Arrays;

public class Piece {

	private static final int FACE_COUNT = 4;
	public static final int TOP = 0;
	public static final int RIGHT = 1;
	public static final int BOTTOM = 2;
	public static final int LEFT = 3;

	private int[] values;
	private char label;
	private int rotation;

	public Piece(char label) {
		this.label = label;
		this.rotation = 0;
		this.values = new int[FACE_COUNT];
	}

	public Piece(char label, int top, int right, int bottom, int left) {
		this(label);
		this.values[Piece.TOP] = top;
		this.values[Piece.RIGHT] = right;
		this.values[Piece.BOTTOM] = bottom;
		this.values[Piece.LEFT] = left;
	}

	public int getValueAt(int direction) {
		return this.values[direction];
	}

	public void setValueAt(int direction, int value) {
		this.values[direction] = value;
	}

	public char getLabel() {
		return this.label;
	}
	
	public int getRotation() {
		return this.rotation;
	}
	
	public void rotateClockwise() {
		this.rotation = (this.rotation + 1) % 4;
		int tmp = this.values[FACE_COUNT-1];
		for (int i = FACE_COUNT-1; i > 0; i--) {
			this.values[i] = this.values[i-1];
		}
		this.values[0] = tmp;
	}

	public void rotateCounterClockwise() {
		this.rotation = (this.rotation + FACE_COUNT - 1) % 4;
		
		int tmp = this.values[0];
		for (int i = 0; i < FACE_COUNT-1; i++) {
			this.values[i] = this.values[i+1];
		}
		this.values[FACE_COUNT-1] = tmp;
	}
	
	@Override
	public Piece clone() {
		Piece copy = new Piece(this.label);
		for (int i=0; i<FACE_COUNT; i++)
			copy.values[i] = this.values[i];
		copy.rotation = this.rotation;
		return copy;
	}

	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer();
		bf.append("+----------+\n");
		bf.append(String.format("|    %2d    |\n", getValueAt(TOP)));
		bf.append(String.format("|%2d [%c%d] %2d|\n", getValueAt(LEFT), this.label, this.rotation, getValueAt(RIGHT)));
		bf.append(String.format("|    %2d    |\n", getValueAt(BOTTOM)));
		bf.append("+----------+");
		return bf.toString();
	}
	
	public void toBuffers(StringBuffer[] lines) {
		lines[0].append("+----------+");
		lines[1].append(String.format("|    %2d    |", getValueAt(TOP)));
		lines[2].append(String.format("|%2d [%c%d] %2d|", getValueAt(LEFT), this.label, this.rotation, getValueAt(RIGHT)));
		lines[3].append(String.format("|    %2d    |", getValueAt(BOTTOM)));
		lines[4].append("+----------+");
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other.getClass() != this.getClass())
			return false;
		Piece o = (Piece) other;
		return Arrays.equals(this.values, o.values) && (this.rotation == o.rotation) && (this.label == o.label);
	}

	public boolean isPerfect() {
		// check if half of the element are positive
		int count = 0;
		for (int i = 0; i < FACE_COUNT; i++) {
			if (this.values[i] >= 0)
				count++;
		}		
		if (count != FACE_COUNT / 2)
			return false;

		// check if we have one occurrence of each element
		int[] absValues = new int[FACE_COUNT];
		for (int i = 0; i < FACE_COUNT; i++) {
			absValues[i] = Math.abs(this.values[i]);
		}
		Arrays.sort(absValues);
		for (int i = 0; i < FACE_COUNT; i++) {
			if (absValues[i] != i + 1)
				return false;
		}

		return true;
	}
}
