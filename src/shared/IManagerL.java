package shared;

import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IManagerL {
    public String talkWithCustomer();
    public void handCarKey(int car);
    public void registerService();
    public int currentCustomer();
    public void checkWhatToDo();
    public int getIdToCall();
	public void receivePayment();

	public void getNextTask();

	public void appraiseSit();

	public Piece getPieceToReStock();
}
