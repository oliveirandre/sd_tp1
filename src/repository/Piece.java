 package repository;

import java.util.Random;

/**
 *
 * @author Andre and Joao
 */
public class Piece {
	
	EnumPiece p;
	
	public Piece(){
		p = EnumPiece.values()[new Random().nextInt(EnumPiece.values().length)];
	}
	
	public EnumPiece getTypePiece(){
		return p;
	}
	
	public int getIdTypePiece(){
		return p.ordinal();
	}
		
}
