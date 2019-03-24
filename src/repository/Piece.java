package repository;

import java.util.Random;

/**
 *
 * @author Andre and Joao
 */
public class Piece {

    EnumPiece p;

	/**
	 * Piece's constructor that initializes a new piece from a random EnumPiece.
	 */
	public Piece() {
        p = EnumPiece.values()[new Random().nextInt(EnumPiece.values().length)];
    }

	/**
	 * Retrieves a new typePiece
	 * @return a EnumPiece
	 */
	public EnumPiece getTypePiece() {
        return p;
    }

	/**
	 * Retrieves the id of piece.
	 * @return id of piece
	 */
	public int getIdTypePiece() {
        return p.ordinal();
    }

    @Override
    public String toString() {
        return this.p.toString();
    }
}
