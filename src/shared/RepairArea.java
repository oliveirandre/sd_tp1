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
    
	/**
	 * Mechanic's method. Reads the paper while there is no work.
	 * When a new car is added to the queue, he exits this method.
	 *
	 */
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

	/**
	 * Mechanic's method. Change the state to start to fix the car.
	 *
	 */
	@Override
    public synchronized void startRepairProcedure() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
    }

	/**
	 * Mechanic's method. Mechanic removes one piece from stock
	 * to repair the car in question
	 * 
	 * 
	 * @param id
	 * @param piece
	 */
	@Override
    public synchronized void fixIt(int id, Piece piece) {
        RepairShop.removePieceFromStock(piece);
        pieceToBeRepaired.remove(id, piece);
    }

	/**
	 * Mechanic's method. Mechanic checks the car and finds what piece it needs.
	 * If no required piece is associated to a car, it creates a new one. 
	 * 
	 * @param id
	 * @return the HashMap containing the cars with the required piece, respectively
	 */
	@Override
    public synchronized HashMap getRequiredPart(int id) {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.CHECKING_STOCK);
        pieceToBeRepaired.putIfAbsent(id, new Piece());
        return pieceToBeRepaired;
    }

	/**
	 * Mechanic's method. After receiving a new car to fix, checks if the required
	 * piece is available in stock.
	 * 
	 * @param part
	 * @return returns true if the piece is available and false otherwise
	 */
	@Override
    public boolean partAvailable(Piece part) {
        return RepairShop.pieceInStock(part);
    }
	
	
	
    @Override
    public synchronized void letManagerKnow() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
        notify();
    }
	
	/**
	 * Mechanic's method. After knowing that the required part is available in 
	 * stock, the mechanic's state changes to fix the car
	 * 
	 */
    @Override
    public synchronized void resumeRepairProcedure() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
    }

	/**
	 * Mechanic's method. Change the state to alert the manager that the
	 * repair is concluded.
	 *
	 */
    @Override
    public synchronized void repairConcluded() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
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
