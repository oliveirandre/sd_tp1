package repository;

import java.util.HashMap;
import static repository.RepairShop.N_OF_REPLACEMENT_CARS;

/**
 *
 * @author Andre e Joao
 */
public class RepairShop {

	public static final int N_OF_CUSTOMERS = 30;
	public static final int N_OF_MECHANICS = 2;
	public static final int N_OF_MANAGERS = 1;
	public static final int N_OF_REPLACEMENT_CARS = 3;
	public static final int N_OF_TYPE_PIECES = 3;

	static int N_OF_PIECES = (int) (Math.random() * 13) + 3; //between 3 and 15 Math.random() * ((max - min) + 1)) + min

	Car[] cars = new Car[N_OF_REPLACEMENT_CARS];

	static HashMap<EnumPiece, Integer> stock = new HashMap<>();

	public RepairShop() {

		for (int i = 0; i < N_OF_REPLACEMENT_CARS; i++) {
			cars[i] = new Car();
		}

		for (int i = 0; i < N_OF_TYPE_PIECES; i++) {
			stock.put(EnumPiece.values()[i], i);
		}

		for (int i = 0; i < N_OF_PIECES; i++) {
			Piece pec = new Piece();

			stock.put(pec.getTypePiece(), stock.get(pec.getTypePiece()) + 1);

		}
	}

	public static HashMap getPieces() {
		return stock;
	}

	public static boolean pieceInStock(Piece p) {
		return stock.containsKey(p.getTypePiece()) && stock.get(p.getTypePiece()) != 0;
	}

	public void removePieceFromStock(Piece p) {
		for (int i = 0; i < N_OF_PIECES; i++) {
			if (pieceInStock(p)) {
				stock.put(p.getTypePiece(), stock.get(p.getTypePiece()) - 1);
			}
		}
	}
}
