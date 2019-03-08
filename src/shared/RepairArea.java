package shared;

import entities.Mechanic;
import entities.MechanicState;

/**
 *
 * @author andre and joao
 */
public class RepairArea implements IRepairArea{

	@Override
	public void readThePaper() {
		((Mechanic)Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
	}

	@Override
	public void startRepairProcedure() {
		((Mechanic)Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
	}

	@Override
	public void getVehicle() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void fixIt() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void returnVehicle() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void getRequiredPart() {
		//escolher randomly qual a peça que vai ser precisa
		
		
		
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean partAvailable() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void letManagerKnow() {
		((Mechanic)Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
		notify();
	}

	@Override
	public void resumeRepairProcedure() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void repairConcluded() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
    
}
