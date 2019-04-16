package shared;

import repository.Piece;
import entities.ManagerState;

/**
 *
 * @author andre e joao
 */
public interface IManagerL {

    public String talkWithCustomer(boolean availableCar);

    public void handCarKey(int replacementCarId, int idCustomer);

    public int currentCustomer(ManagerState state);

    public void checkWhatToDo(ManagerState state);

    public int getIdToCall(ManagerState state);

    public boolean enoughWork();

    public boolean alertCustomer(int id);

    public void getNextTask();

    public void receivePayment();

    public int appraiseSit();

    public Piece getPieceToReStock(ManagerState state);

    public void goReplenishStock(ManagerState state);
}
