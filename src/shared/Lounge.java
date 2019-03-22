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
    private final Queue<Piece> mechanicsQueue = new LinkedList<>();
    private final Queue<Integer> carsToRepair = new LinkedList<>();
    private int nextCustomer = 0;
    private Piece pieceToReStock;
    private boolean payed = false;
    private boolean managerAvailable = false;
    private boolean ordered = false;
    private boolean call = false;
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
        while(nextCustomer != id && !managerAvailable) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
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
        nextCustomer = customersQueue.poll();
        //System.out.println(nextCustomer);
        managerAvailable = true;
        notifyAll();
        managerAvailable = false;
        // fiquei aqui
        //System.out.println("Manager - Attending customer number " + nextCustomer);
        //notifyAll();
        while (!(order.containsKey(nextCustomer)) && !ordered) {
            try {
                //System.out.println("SERA AQUI");
                wait();
            } catch (Exception e) {

            }
        }
        String s = order.get(nextCustomer);
        //System.out.println("HDAUIDHW "+ s);
        order.remove(nextCustomer);
        return s;

    }

    @Override
    public synchronized void handCarKey() {
        customerGetRepCar = replacementQueue.poll();
        //notifyAll();
    }

    @Override
    public synchronized void payForTheService() {
        order.put(nextCustomer, "pay");
        /*payed = true;
        notify();
        payed = false;*/
    }

    /*
    ** Manager's method. After receiving a request from a customer, the manager 
    ** registers it for further use by the mechanics.
     */
    @Override
    public synchronized void registerService() {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.POSTING_JOB);
        carsToRepair.add(nextCustomer);
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
        while (!carsRepaired.contains(((Customer) Thread.currentThread()).getCustomerId())) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        carsRepaired.remove(((Customer) Thread.currentThread()).getCustomerId());
        //if(carsRepaired.contains(((Customer) Thread.currentThread()).getCustomerId())) {
            ((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
            ((Customer) Thread.currentThread()).carRepaired = true;
            replacementQueue.remove(((Customer) Thread.currentThread()).getCustomerId());
            //System.out.println("azar do caralho");
        //}
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
        /*while (!payed) {
            try {
                wait();
            } catch (Exception e) {

            }
        }*/
    }

    @Override
    public synchronized void appraiseSit() {
        if (!mechanicsQueue.isEmpty()) {

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

    @Override
    public synchronized Queue getCarsToRepair() {
        return carsToRepair;
    }

    @Override
    public synchronized void alertManager(Piece piece, int idCar) {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        if (piece == null) { //repair of this carId is concluded
            customersToCallQueue.add(idCar);
        } else {
            pieceToReStock = piece;
        }
        //.println("Customers to call : " + customersToCallQueue.toString());
        call = true;
        notifyAll();
        call = false;
    }

    @Override
    public synchronized Piece getPieceToReStock() {
        Piece temp = pieceToReStock;
        pieceToReStock = null; //put null since piece is already going to stock when this method is called
        return temp;
    }
    
    @Override
    public synchronized boolean enoughWork() {
        if(customersQueue.isEmpty() && replacementQueue.isEmpty())
            return true;
        else
            return false;
    }
    
	/**
	 * DO MANAGER PARA CUSTOMER
	 * @param id
	 */
	@Override
    public synchronized void alertCustomer(int id) {
        if(replacementQueue.contains(id)) {
            carsRepaired.add(id);
            //System.out.println(carsRepaired.toString());
            notifyAll();
            customersToCallQueue.remove(id);
        }
    }
}
