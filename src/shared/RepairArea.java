package shared;

import entities.Manager;
import entities.ManagerState;
import entities.Mechanic;
import entities.MechanicState;
import repository.Piece;
import repository.RepairShop;

/**
 *
 * @author andre and joao
 */


public class RepairArea implements IRepairArea {

	Piece[] p;
	
	
	
	@Override
	public synchronized void readThePaper() {
		((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
		while (true) {
			try {
				wait();
			} catch (Exception e) {

			}
		}
	}

	@Override
	public synchronized void startRepairProcedure() {
		((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
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
	public synchronized Piece getRequiredPart() {
		((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.CHECKING_STOCK);
		return new Piece();
	}

	@Override
	public synchronized boolean partAvailable(Piece part) {
		return RepairShop.pieceInStock(part);
	}

	@Override
	public synchronized void letManagerKnow() {
		((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
		notify();
	}

	@Override
	public synchronized void resumeRepairProcedure() {
		((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void repairConcluded() {
		((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void getNextTask() {
		((Manager) Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void storePart() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
