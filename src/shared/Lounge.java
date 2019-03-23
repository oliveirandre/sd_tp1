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

/**
 *
 * @author andre and joao
 */
public class Lounge implements ICustomerL, IManagerL, IMechanicL {

    private int customerGetRepCar;
    private final Queue<Integer> replacementQueue = new LinkedList<>();
    private final Queue<Integer> customersQueue = new LinkedList<>();
    private int nextCustomer = 0;
    private Piece pieceToReStock;
	private Piece pieceToReStock2;
    private boolean managerAvailable = false;
    private boolean ordered = false;
    private boolean call = false;
    private boolean payed = false;
    private boolean receivePayment = false;
    private boolean enoughWork = false;
    private final Queue<Integer> customersToCallQueue = new LinkedList<>(); //repair Concluded
    private final Queue<Integer> carsRepaired = new LinkedList<>();

    private static HashMap<Integer, String> order = new HashMap<Integer, String>();


	/*
    ** Customer's method. After parking the car in need of a repair, the custo-
    ** mer now has to wait in a queue to be attended by the manager.
     */
    @Override
    public synchronized void queueIn(int id) {
        customersQueue.add(id);
        //System.out.println("Customer " + id + " - Waiting in queue.");
        notifyAll();
        //while(customersQueue.peek() != id && !managerAvailable) {
        //System.out.println("!!!!!!!!!!!!!!!! " + (nextCustomer == id) + " ????????? " + managerAvailable);
        // ERRO É AQUI | THREAD ESTÁ NO FIM DA FILA E AVANÇA PORQUE NEXTCUSTOMER É IGUAL A ELA
        while(nextCustomer != id && !managerAvailable) {
            try {
                wait();
            } catch (Exception e) {

            }                
        }
        //System.out.println("ADIHWADIWA || " + nextCustomer);
        //System.out.println("Customer " + id + " - Attended by manager.");
    }

    /*
    ** Customer's method. When the customer is talking to the manager he says if
    ** he requires a replacement car or not.
     */
    @Override
    public synchronized void talkWithManager() {
        //System.out.println("Talking with manager.");
        
        if (((Customer) Thread.currentThread()).carRepaired) {
            order.put(nextCustomer, "pay");
        } else {
            if (((Customer) Thread.currentThread()).requiresCar) {
                order.put(nextCustomer, "car");
            } else {
                order.put(nextCustomer, "nocar");
            }
        }
        ordered = true;
        //System.out.println(ordered + " | " + order.containsKey(nextCustomer));
        notifyAll();
        ordered = false;
    }

    /*
    ** Manager's method. The manager awakes the customer next in the queue, and
    ** then waits to see if the customer requires or not a replacement car.
     */
    @Override
    public synchronized String talkWithCustomer(boolean availableCar) {
        /*if (customersQueue.isEmpty()) {
            customersQueue.add(replacementQueue.poll());
        }*/
        //System.out.println(nextCustomer);
        nextCustomer = customersQueue.poll();
        //System.out.println(nextCustomer + " | " + customersQueue.toString());
        managerAvailable = true;
        notifyAll();
        managerAvailable = false;
        // fiquei aqui
        System.out.println("Manager - Attending customer number " + nextCustomer);
        //notifyAll();
        while (!(order.containsKey(nextCustomer)) && !ordered) {
            try {
                //System.out.println("SERA AQUI");
                wait();
            } catch (Exception e) {

            }
        }
        //System.out.println(order.toString());
        String s = order.get(nextCustomer);
        //System.out.println("HDAUIDHW "+ s);
        order.remove(nextCustomer);
        nextCustomer = 0;
        return s;

    }

    @Override
    public synchronized void handCarKey() {
        customerGetRepCar = replacementQueue.poll();
        notifyAll();
    }

    @Override
    public synchronized void payForTheService() {
        payed = true;
        //System.out.println("PAYED");
        notifyAll();
        while(!receivePayment) {
            try {
                wait();
            } catch(Exception e){
                
            }
        }      
        receivePayment = false;
    }
    

    @Override
    public synchronized int currentCustomer() {
        if (customersQueue.isEmpty()) {
            return replacementQueue.peek();
        } else {
            return customersQueue.peek();
        }
    }

    @Override
    public synchronized void collectKey() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);
        replacementQueue.add(((Customer) Thread.currentThread()).getCustomerId());
        //System.out.println("Replacement Queue - " + replacementQueue.toString());
        //System.out.println("Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - Waiting for key...");
        // customerGetRepCar != ((Customer) Thread.currentThread()).getCustomerId() && 
        while (customerGetRepCar != ((Customer) Thread.currentThread()).getCustomerId() && !carsRepaired.contains(((Customer) Thread.currentThread()).getCustomerId())) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        if(carsRepaired.contains(((Customer) Thread.currentThread()).getCustomerId())) {
            carsRepaired.remove(((Customer) Thread.currentThread()).getCustomerId());
            replacementQueue.remove(((Customer) Thread.currentThread()).getCustomerId());
            ((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
            ((Customer) Thread.currentThread()).carRepaired = true;
            //System.out.println("azar do caralho");
        }
    }

    @Override
    public synchronized void getNextTask() {
        //System.out.println("Manager - Waiting for next task... <---> \n-> CUSTOMERS "+ customersQueue.toString() + "\n-> TO CALL " + customersToCallQueue.toString());
        //  && mechanicsQueue.isEmpty() && customersToCallQueue.isEmpty() && replacementQueue.isEmpty()
        
        
        while (customersQueue.isEmpty() && !call && customersToCallQueue.isEmpty()) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        //System.out.println("-> NEW CUSTOMER TO CALL " + customersToCallQueue.toString());
    }

    @Override
    public synchronized void checkWhatToDo() {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
    }

    @Override
    public synchronized int getIdToCall() {
        //System.out.println("!!!!!!!!! " + customersToCallQueue.peek());
        return customersToCallQueue.poll();
    }

    @Override
    public synchronized void receivePayment(String s) {
        while (!payed) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        payed = false;
        receivePayment = true;
        notifyAll();
        //System.out.println("Manager - Waiting for payed.");
    }

    @Override
    public synchronized void appraiseSit() {
        if (pieceToReStock!=null) {
			((Manager) Thread.currentThread()).setManagerState(ManagerState.GETTING_NEW_PARTS);
        }
        else if (!customersToCallQueue.isEmpty()) {
            ((Manager) Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
        }
        /*if (!replacementQueue.isEmpty()) {
            System.out.println("REPLACEMENT QUEUE " + replacementQueue.toString());
            ((Manager) Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
            return;
        }*/
        else if(!customersQueue.isEmpty()) {
            //System.out.println("-> CUSTOMERS QUEUE " + customersQueue.toString());
            ((Manager) Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        }
    }

	/**
	 * Mechanic's method. 	
	 * 
	 * @param piece
	 * @param idCar
	 */
	@Override
    public synchronized void alertManager(Piece piece, int idCar) {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        if (piece == null) { //repair of this carId is concluded
            customersToCallQueue.add(idCar);
        } else {
            pieceToReStock = piece;
			
        }
        //System.out.println("Customers to call : " + customersToCallQueue.toString());
        call = true;
        notifyAll();
        call = false;
    }

    @Override
    public synchronized Piece getPieceToReStock() {
		Piece temp = pieceToReStock;
		pieceToReStock2 = pieceToReStock;
		pieceToReStock = null;	//put null since piece is already going to stock when this method is called
        return temp;
    }
	
	@Override
    public synchronized void goReplenishStock() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
    }
    
    @Override
    public synchronized boolean enoughWork() {
        System.out.println(enoughWork);
        if(customersQueue.isEmpty() && replacementQueue.isEmpty() && enoughWork)
            return true;
        else
            return false;
    }
    
	/**
	 * DO MANAGER PARA CUSTOMER
	 * @param id
	 */
	@Override
    public synchronized boolean alertCustomer(int id) {
        if(replacementQueue.contains(id)) {
            carsRepaired.add(id);
            System.out.println("CARS REPAIRED " + carsRepaired.toString());
            notifyAll();
            customersToCallQueue.remove(id);
            return true;
        }
        else {
            return false;
        }
    }
	
	public int getCustomersQueueSize(){
		return customersQueue.size();
	}
	
	public int getCustomersReplacementQueueSize(){
		return replacementQueue.size();
	}
	
	public int getCarsRepairedSize(){
		return carsRepaired.size();
	}
	
	public int getPieceMissingId(){
		return pieceToReStock2.getTypePiece().ordinal();
	}
}
