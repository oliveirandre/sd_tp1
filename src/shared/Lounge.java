package shared;

import entities.Customer;
import entities.CustomerState;
import entities.Manager;
import entities.ManagerState;
import entities.Mechanic;
import entities.MechanicState;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import repository.Piece;
import repository.RepairShop;

/**
 *
 * @author andre and joao
 */

public class Lounge implements ICustomerL, IManagerL, IMechanicL {
    
    private RepairShop repairShop;
    
    private Queue<Integer> replacementQueue;
    private final Queue<Integer> customersQueue = new LinkedList<>();
    private static Queue<Integer> carsToRepair = new LinkedList<>();
    private int nextCustomer;
	private Piece pieceToReStock;
	private Queue<Integer> customersToCallQueue = new LinkedList<>(); //repair Concluded
    
    private static HashMap<Integer, Boolean> requiresCar = new HashMap<Integer, Boolean>();


    /*
    ** Customer's method. After parking the car in need of a repair, the custo-
    ** mer now has to wait in a queue to be attended by the manager.
    */       
    @Override
    public synchronized void queueIn(int id) {
        customersQueue.add(id);
        notify();
        System.out.println("Customer " + id + " - Waiting in queue.");
        while(!(nextCustomer == id)) {
            try {
                wait();
                if(nextCustomer == id) {
                    System.out.println("Customer " + id + " - Attended by manager.");
                    return;
                }
            } catch(Exception e) {
                
            }
        }
    }
    
    /*
    ** Customer's method. When the customer is talking to the manager he says if
    ** he requires a replacement car or not.
    */
    @Override
    public synchronized void talkWithManager() {
        requiresCar.put(nextCustomer, ((Customer)Thread.currentThread()).requiresCar);
        notifyAll();
        if(((Customer)Thread.currentThread()).requiresCar) {
            while(true) {
                try {
                    wait();
                } catch(Exception e) {
                    
                }
            }
        }
        return;
    }    
    
    /*
    ** Manager's method. The manager awakes the customer next in the queue, and
    ** then waits to see if the customer requires or not a replacement car.
    */
    @Override
    public synchronized boolean talkWithCustomer() {
        //((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        nextCustomer = customersQueue.poll();
        System.out.println("Manager - Attending customer number " + nextCustomer);
        notifyAll();
        while(!(requiresCar.containsKey(nextCustomer))) {
            try {
                wait();
                if(requiresCar.containsKey(nextCustomer)) {
                    if(requiresCar.get(nextCustomer) == true) {
                        replacementQueue.add(nextCustomer);
                        return true;            
                    }
                    else
                        return false;
                }
            } catch(Exception e) {
                
            }
        }
        return false;
    }

    @Override
    public synchronized void handCarKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void payForTheService() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /*
    ** Manager's method. After receiving a request from a customer, the manager 
    ** registers it for further use by the mechanics.
    */
    @Override
    public synchronized void registerService() {
        carsToRepair.add(nextCustomer);
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.POSTING_JOB);
    }    
    
    @Override
    public synchronized void collectKey() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);
        if(RepairShop.N_OF_REPLACEMENT_CARS > 0) {
            RepairShop.N_OF_REPLACEMENT_CARS--;
            return;
        }
        while(true) {
            try {
                wait();
                if(replacementQueue.peek() == ((Customer)Thread.currentThread()).getCustomerId()) {
                    
                }
            } catch(Exception e) {
                
            }
        }
    }
    
    
    @Override
    public synchronized void getNextTask() {
        // manager gets the next task: can be talkToCustomer, phoneCustomer, goToSupplier
        if(pieceToReStock!=null)
			((Manager)Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
		else if(!customersToCallQueue.isEmpty())
			((Manager)Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
		else if(!customersQueue.isEmpty())
            ((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
		else
			((Manager)Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
        
    }
    
    @Override
    public synchronized void receivePayment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    @Override
    public synchronized void appraiseSit() {
        System.out.println("Manager - Appraising sit.");
        while(customersQueue.isEmpty()) {
            try {
                wait();
                if(!customersQueue.isEmpty()) {
                    System.out.println("Manager - Customer waiting!");
                    return;
                }
            } catch(Exception e) {
                
            }
        }
    }
    
    @Override
    public synchronized void phoneCustomer() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
		notify();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized Queue getCarsToRepair(){
        return carsToRepair;
    }

	@Override
	public synchronized void alertManager(Piece piece, int idCar) {
		((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
		if(piece==null){ //repair of this carId is concluded
			customersToCallQueue.add(idCar);
		}else{
			pieceToReStock = piece;
		}
		notify();
	}
	
	@Override
	public synchronized Piece getPieceToReStock(){
		Piece temp = pieceToReStock;
		pieceToReStock = null; //put null since piece is already going to stock when this method is called
		return temp;
	}
}
