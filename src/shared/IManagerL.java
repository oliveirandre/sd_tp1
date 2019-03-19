package shared;

import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IManagerL {
    public boolean talkWithCustomer();
    public void handCarKey();
    public void registerService();

	public void receivePayment();

	public void getNextTask();

	public void phoneCustomer();

	public void appraiseSit();

	public Piece getPieceToReStock();
}
