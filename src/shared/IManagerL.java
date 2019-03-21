package shared;

import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IManagerL {
    public String talkWithCustomer();
    public void handCarKey();
    public void registerService();
    public int currentCustomer();
    public void checkWhatToDo();
    public int getIdToCall();
	public void receivePayment(String s);

	public void getNextTask();

	public void appraiseSit();

	public Piece getPieceToReStock();
}
