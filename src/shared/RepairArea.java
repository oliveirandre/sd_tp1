package shared;

import entities.Manager;
import entities.ManagerState;
import entities.Mechanic;
import entities.MechanicState;
import java.util.HashMap;
import repository.Piece;
import repository.RepairShop;

/**
 *
 * @author andre and joao
 */
public class RepairArea implements IMechanicRA, IManagerRA {

    HashMap<Integer, Piece> pieceToBeRepaired = new HashMap<>();
    
    @Override
    public synchronized void readThePaper() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        while (Lounge.getCarsToRepair().isEmpty()) { //while there is no car to repair
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
    public synchronized int getVehicle() {
        return (int) Lounge.getCarsToRepair().poll();
    }

    @Override
    public synchronized void fixIt(int id, Piece part) {
        RepairShop.removePieceFromStock(part);
        pieceToBeRepaired.remove(id, part);
    }

    @Override
    public synchronized HashMap getRequiredPart(int id) {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.CHECKING_STOCK);
        pieceToBeRepaired.putIfAbsent(id, new Piece());
        return pieceToBeRepaired;
    }

    @Override
    public boolean partAvailable(Piece part) {
        return RepairShop.pieceInStock(part);
    }

    @Override
    public synchronized void letManagerKnow() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
        notify();
    }

    @Override
    public synchronized boolean resumeRepairProcedure() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
		return true;
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

    /*@Override
    public void storePart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
*/

    
}
