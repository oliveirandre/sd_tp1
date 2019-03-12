package shared;

import entities.Mechanic;
import entities.MechanicState;

/**
 *
 * @author andre and joao
 */
public class RepairArea implements IRepairArea{

	@Override
	public synchronized void readThePaper() {
            ((Mechanic)Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
            while(true) {
                try {
                    wait();
                } catch(Exception e) {
                    
                }                
            }
	}

	@Override
	public synchronized void startRepairProcedure() {
            ((Mechanic)Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
	}

	@Override
	public synchronized void getVehicle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void fixIt() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void returnVehicle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void getRequiredPart() {
            //escolher randomly qual a peça que vai ser precisa
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized boolean partAvailable() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void letManagerKnow() {
            ((Mechanic)Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
            notify();
	}

	@Override
	public synchronized void resumeRepairProcedure() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void repairConcluded() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
    
}
