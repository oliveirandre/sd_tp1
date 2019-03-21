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
    private boolean availableCar = false;
    private Queue<Integer> replacementQueue;
    private final Queue<Integer> customersQueue = new LinkedList<>();
    private final Queue<Piece> mechanicsQueue = new LinkedList<>();
    private static Queue<Integer> carsToRepair = new LinkedList<>();
    private int nextCustomer;
    private Piece pieceToReStock;
    private Queue<Integer> customersToCallQueue = new LinkedList<>(); //repair Concluded
    
    private static HashMap<Integer, String> order = new HashMap<Integer, String>();


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
        if(((Customer)Thread.currentThread()).carRepaired)
            order.put(nextCustomer, "pay");
        else {
            if(((Customer)Thread.currentThread()).requiresCar) {
                order.put(nextCustomer, "car");
            }
            else
                order.put(nextCustomer, "nocar");
        }
        notifyAll();
        /*if(((Customer)Thread.currentThread()).requiresCar) {
            while(true) {
                try {
                    wait();
                } catch(Exception e) {
                    
                }
            }
        }
        return;*/
    }    
    
    /*
    ** Manager's method. The manager awakes the customer next in the queue, and
    ** then waits to see if the customer requires or not a replacement car.
    */
    @Override
    public synchronized String talkWithCustomer() {
        //((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        nextCustomer = customersQueue.poll();
        System.out.println("Manager - Attending customer number " + nextCustomer);
        notifyAll();
        while(!(order.containsKey(nextCustomer))) {
            try {
                wait();
                if(order.containsKey(nextCustomer)) {
                    String s = order.get(nextCustomer);
                    if(s.equals("car")) {
                        //replacementQueue.add(nextCustomer);
                        return s;        
                    }
                    else {                       
                        return s;    
                    }
                }
            } catch(Exception e) {
                
            }
        }
        return "";
    }

    @Override
    public synchronized void handCarKey(int car) {
        if(car != 0) {
            availableCar = true;
            notifyAll();
        }
    }
    
    @Override
    public synchronized void payForTheService() {
        notifyAll();    
    }
    
    /*
    ** Manager's method. After receiving a request from a customer, the manager 
    ** registers it for further use by the mechanics.
    */
    @Override
    public synchronized void registerService() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.POSTING_JOB);
        carsToRepair.add(nextCustomer);
    }    
    
    @Override
    public int currentCustomer() {
        return customersQueue.peek();
    }
    
    @Override
    public synchronized void collectKey() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);
        System.out.println("Waiting for key...");
        while(!availableCar) {
            try {
                wait();
                if(availableCar) {
                    System.out.println("have key for car " + availableCar);
                    return;
                }
            } catch(Exception e) {
                
            }
        }
        /*if(RepairShop.N_OF_REPLACEMENT_CARS > 0) {
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
        }*/
    }
    
    
    @Override
    public synchronized void getNextTask() {
        // manager gets the next task: can be talkToCustomer, phoneCustomer, goToSupplier
        /*if(pieceToReStock!=null)
			((Manager)Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
		else if(!customersToCallQueue.isEmpty())
			((Manager)Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
		else if(!customersQueue.isEmpty())
            ((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
		else
			((Manager)Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
        */
        System.out.println("Manager - Waiting for next task...");
        while(customersQueue.isEmpty() && mechanicsQueue.isEmpty() && customersToCallQueue.isEmpty()) {
            try {
                wait();
                if(!customersQueue.isEmpty() || !mechanicsQueue.isEmpty() || !customersToCallQueue.isEmpty())
                    return;
            } catch(Exception e) {
                
            }
        }
    }
    
    @Override
    public synchronized void checkWhatToDo() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
    }
    
    @Override
    public synchronized int getIdToCall() {
        return customersToCallQueue.poll();
    }
    
    @Override
    public synchronized void receivePayment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    @Override
    public synchronized void appraiseSit() {
        if(!mechanicsQueue.isEmpty()) {
            
        }
        if(!customersToCallQueue.isEmpty()) {
            ((Manager)Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
            return;
        }
        if(!customersQueue.isEmpty()) {
            ((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        }
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
		notifyAll();
	}
	
	@Override
	public synchronized Piece getPieceToReStock(){
		Piece temp = pieceToReStock;
		pieceToReStock = null; //put null since piece is already going to stock when this method is called
		return temp;
	}
}
