package shared;

import entities.Manager;
import entities.ManagerState;
import repository.Piece;

/**
 *
 * @author andre and joao
 */
public class SupplierSite implements IManagerSS {
    
	int randomNum = 1;//1 + (int)(Math.random() * ((5 - 1) + 1)); //between 1 and 6////
	Piece partNeeded;
	
	int[] piecesBought;
	
	public SupplierSite(int nTypePieces){
		piecesBought = new int[nTypePieces];
		for (int i = 0; i < nTypePieces; i++) {
			piecesBought[i]=0;
		}
	}
	
	
	@Override
    public synchronized int goToSupplier(Piece partNeeded) {
		this.partNeeded = partNeeded;
		piecesBought[partNeeded.getTypePiece().ordinal()]+=randomNum;
		return randomNum;
    }
	
	public int[] getPiecesBought() {
		return piecesBought;
	}
}
