package puzzle;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator implements Iterator<Piece> {

	private Piece[] elements;
	private int index = 0;

	public ArrayIterator(Piece[] elements) {
		this.elements = elements;
	}

	public boolean hasNext() {
		return (this.index < this.elements.length);
	}

	public Piece next() throws NoSuchElementException {
		if (this.index >= this.elements.length)
			throw new NoSuchElementException(String.format("Index greater than length %d > %d", this.index,
					this.elements.length));
		Piece piece = this.elements[index];
		this.index++;
		return piece;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}