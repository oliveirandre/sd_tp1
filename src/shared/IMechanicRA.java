package shared;

import java.util.HashMap;
import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IMechanicRA {
    public void readThePaper();
    public void startRepairProcedure();
    
    public HashMap getRequiredPart(int id);
    
    public Piece letManagerKnow(Piece piece);

    public boolean partAvailable(Piece requiredPart);
    
    public void fixIt(int id, Piece p);
    
    public void getNextTask();

    public void resumeRepairProcedure();
    public void repairConcluded();
    
}
