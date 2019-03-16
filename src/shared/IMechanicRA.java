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
    
    public void letManagerKnow();

    public boolean partAvailable(Piece requiredPart);
    
    public int getVehicle();
    public void fixIt(int id, Piece p);
    
    public void getNextTask();

    public boolean resumeRepairProcedure();
    public void repairConcluded();
    //public void storePart();
    
}
