package shared;

import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IManagerRA {
	public void registerService(int idCustomer);
	public int storePart(Piece part, int quant);
    public void enoughWork();
}
