package shared;

import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IManagerL {
    public String talkWithCustomer(boolean availableCar);
    public void handCarKey();
    public void registerService();
    public int currentCustomer();
    public void checkWhatToDo();
    public int getIdToCall();
    public boolean enoughWork();
    public void alertCustomer(int id);
	public void receivePayment(String s);

	public void getNextTask();

	public void appraiseSit();

	public Piece getPieceToReStock();
}
