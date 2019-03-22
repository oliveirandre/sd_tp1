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
    private Queue<Integer> replacementQueue = new LinkedList<>();
    private final Queue<Integer> customersQueue = new LinkedList<>();
    private final Queue<Piece> mechanicsQueue = new LinkedList<>();
    private static Queue<Integer> carsToRepair = new LinkedList<>();
    private int nextCustomer;
    private Piece pieceToReStock;
    private boolean payed = false;
    private Queue<Integer> customersToCallQueue = new LinkedList<>(); //repair Concluded
    private Queue<Integer> carsRepaired = new LinkedList<>();

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
        while (!(nextCustomer == id)) {
            try {
                wait();
                /*if(nextCustomer == id) {
                    return;
                }*/
            } catch (Exception e) {

            }
        }
        System.out.println("Customer " + id + " - Attended by manager.");
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
			System.out.println("PILOCAS DAS DURAS: " + ((Customer) Thread.currentThread()).requiresCar);
            if (((Customer) Thread.currentThread()).requiresCar) {
				
                order.put(nextCustomer, "car");
            } else {
                order.put(nextCustomer, "nocar");
            }
        }
        notify();
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
    public synchronized String talkWithCustomer(boolean availableCar) {
        //((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        //System.out.println("Customers Queue - " + customersQueue.toString() + " - ");
        //System.out.println("Replacement Queue - " + replacementQueue.toString() + " - ");
        /*if(!replacementQueue.isEmpty() && availableCar) {
            customerGetRepCar = replacementQueue.poll();
            notifyAll();
            return order.get(customerGetRepCar);
        }*/

        if (customersQueue.isEmpty()) {
            customersQueue.add(replacementQueue.poll());
        }
        nextCustomer = customersQueue.poll();

        //System.out.println("Manager - Attending customer number " + nextCustomer);
        notifyAll();
        while (!(order.containsKey(nextCustomer))) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        String s = order.get(nextCustomer);
        order.remove(nextCustomer);
        return s;

    }

    @Override
    public synchronized void handCarKey() {
        //System.out.println("ACORDA " + replacementQueue.peek());
        customerGetRepCar = replacementQueue.poll();
        notifyAll();
    }

    @Override
    public synchronized void payForTheService() {
        order.put(nextCustomer, "pay");
        payed = true;
        notify();
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
        System.out.println("Replacement Queue - " + replacementQueue.toString());
        System.out.println("Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - Waiting for key...");
        while (customerGetRepCar != ((Customer) Thread.currentThread()).getCustomerId() && !carsRepaired.contains(((Customer) Thread.currentThread()).getCustomerId())) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        if(carsRepaired.contains(((Customer) Thread.currentThread()).getCustomerId())) {
            replacementQueue.remove(((Customer) Thread.currentThread()).getCustomerId());
            System.out.println("azar do caralho");
            ((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
            ((Customer) Thread.currentThread()).carRepaired = true;
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
        System.out.println("Manager - Waiting for next task... <---> "+ customersQueue.toString());
        
        while (customersQueue.isEmpty() && mechanicsQueue.isEmpty() && customersToCallQueue.isEmpty() && replacementQueue.isEmpty()) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        //System.out.println("NEW CUSTOMER " + customersQueue.toString());
    }

    @Override
    public synchronized void checkWhatToDo() {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
    }

    @Override
    public synchronized int getIdToCall() {
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
    }

    @Override
    public synchronized void appraiseSit() {
        if (!mechanicsQueue.isEmpty()) {

        }
        if (!customersToCallQueue.isEmpty()) {
            ((Manager) Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
            return;
        }
        if (!replacementQueue.isEmpty()) {
            System.out.println("REPLACEMENT QUEUE " + replacementQueue.toString());
            ((Manager) Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
            return;
        }
        if (!customersQueue.isEmpty()) {
            System.out.println("CUSTOMERS QUEUE " + customersQueue.toString());
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
        notify();
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
            notifyAll();
        }
    }
}
