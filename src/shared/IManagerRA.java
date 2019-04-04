package shared;

import entities.ManagerState;
import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IManagerRA {

    public void registerService(int idCustomer, ManagerState state);

    public int storePart(Piece part, int quant);

    public void enoughWork();
}
