package shared;
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
	public void getRequiredPart();
	
	public boolean partAvailable();
	public void letManagerKnow();
	
	public void resumeRepairProcedure();
	public void repairConcluded();
	
	
}
