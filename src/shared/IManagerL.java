package shared;

import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IManagerL {

    public String talkWithCustomer(boolean availableCar);

    public void handCarKey();

    public int currentCustomer();

    public void checkWhatToDo();

    public int getIdToCall();

    public boolean enoughWork();

    public boolean alertCustomer(int id);

    public void getNextTask();

    public void receivePayment();

    public void appraiseSit();

    public Piece getPieceToReStock();

    public void goReplenishStock();
}
