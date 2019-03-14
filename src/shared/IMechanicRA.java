package shared;

import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IMechanicRA {
    public void readThePaper();
    public void startRepairProcedure();
    
    public Piece getRequiredPart();
    public boolean partAvailable();
    public void letManagerKnow();

    public boolean partAvailable(Piece requiredPart);
    
    public int getVehicle();
    public void fixIt(Piece p);
    
    public void getNextTask();

    public void resumeRepairProcedure();
    public void repairConcluded();
    //public void storePart();
    
}
