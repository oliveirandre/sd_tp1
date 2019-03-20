package shared;

import java.util.HashMap;
import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IMechanicRA {
    public void readThePaper();
    public int startRepairProcedure();
    
    public HashMap getRequiredPart(int id);
    
    public void letManagerKnow();

    public boolean partAvailable(Piece requiredPart);
    
    public void fixIt(int id, Piece p);
    
    public void getNextTask();

    public void resumeRepairProcedure();
    public void repairConcluded();
	
	public int getIdFromManager();    
}
