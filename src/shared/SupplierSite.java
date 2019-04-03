package shared;

import entities.Manager;
import entities.ManagerState;
import repository.Piece;
import repository.RepairShop;

/**
 *
 * @author andre and joao
 */
public class SupplierSite implements IManagerSS {

    private Piece partNeeded;
    private int[] piecesBought;
	private RepairShop repairShop;

    /**
     * SupplierSite constructor. Initializes the array containing pieces bought
     * by the manager over time.
     *
     * @param nTypePieces number of type of pieces
     */
    public SupplierSite(int nTypePieces, RepairShop repairShop) {
        this.repairShop = repairShop;
		piecesBought = new int[nTypePieces];
        for (int i = 0; i < nTypePieces; i++) {
            piecesBought[i] = 0;
        }
		repairShop.updateFromSupplierSite(piecesBought);
    }

    /**
     * Manager's method. Manager goes into supplier site and buys the required
     * pieces in a random amount.
     *
     * @param partNeeded a piece that the manager needs to buy
     * @return an Integer representing the amount of pieces he bought
     */
    @Override
    public synchronized int goToSupplier(Piece partNeeded) {
        int randomNum = 1 + (int) (Math.random() * ((5 - 1) + 1)); //between 1 and 6////
        this.partNeeded = partNeeded;
        piecesBought[partNeeded.getTypePiece().ordinal()] += randomNum;
		repairShop.updateFromSupplierSite(piecesBought);
        return randomNum;
    }

    /**
     * Method used for log. Retrieves the total number of pieces bought by the
     * manager for each type of piece.
     *
     * @return an Array with the total number of pieces bought for each type of
     * piece
     */
    public int[] getPiecesBought() {
        return piecesBought;
    }
}
