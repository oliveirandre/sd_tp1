package shared;

import repository.Piece;

/**
 *
 * @author andre and joao
 */
public interface IRepairArea {
    public void readThePaper();
	
	public void startRepairProcedure();
	public void getVehicle();
	public void fixIt();
	public void returnVehicle();
    public void getNextTask();
	public Piece getRequiredPart();
	
	public boolean partAvailable(Piece p);
	public void letManagerKnow();
	
	public void resumeRepairProcedure();
	public void repairConcluded();

	public void storePart();
	
	
}
