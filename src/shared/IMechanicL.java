package shared;

import java.util.Queue;
import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IMechanicL {
	public void alertManager(Piece piece, int idCar);

	public Queue getCarsToRepair();
}
