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
    private final Queue<Piece> piecesQueue = new LinkedList<>();
    private int nextCustomer = 0;
    private boolean managerAvailable = false;
    private boolean ordered = false;
    private boolean payed = false;
    private final boolean enoughWork = false;
    private final Queue<Integer> customersToCallQueue = new LinkedList<>();
    private final Queue<Integer> carsRepaired = new LinkedList<>();
    private final boolean[] flagPartMissing;
    private boolean readyToReceive;

    private static HashMap<Integer, String> order = new HashMap<Integer, String>();

	/**
	 *
	 * @param nTypePieces
	 */
	public Lounge(int nTypePieces) {
        flagPartMissing = new boolean[nTypePieces];
    }

    /**
     * Customer's method. After parking the car in need of a repair, the
     * customer now has to wait in a queue to be attended by the manager.
     *
     * @param id customer's id
     */
    @Override
    public synchronized void queueIn(int id) {
        customersQueue.add(id);
        notifyAll();
        while (nextCustomer != id && !managerAvailable) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
    }

    /**
     * Customer's method. When the customer is talking to the manager he says if
     * he requires a replacement car or not.
     */
    @Override
    public synchronized void talkWithManager() {
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
        notifyAll();
        ordered = false;
    }

    /**
     * Manager's method. The manager awakes the customer next in the queue, and
     * then waits to see if the customer requires a replacement car.
     *
     * @param availableCar boolean representing if there is a replacement car
     * available
     * @return a String containing the information about what the customer needs
     * to do
     */
    @Override
    public synchronized String talkWithCustomer(boolean availableCar) {
        nextCustomer = customersQueue.poll();
        managerAvailable = true;
        notifyAll();
        managerAvailable = false;
        while (!(order.containsKey(nextCustomer)) && !ordered) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        String s = order.get(nextCustomer);
        order.remove(nextCustomer);
        nextCustomer = 0;
        return s;

    }

    /**
     * Manager's method. Manager gives to the customer the replacement car's
     * key.
     *
     */
    @Override
    public synchronized void handCarKey() {
        while (replacementQueue.isEmpty()) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        customerGetRepCar = replacementQueue.poll();
        notifyAll();
    }

    /**
	 * Customer's method. Customer waits until manager is ready to 
	 * receive payment, and then proceeds to pay.
	 */
    @Override
    public synchronized void payForTheService() {
        while (!readyToReceive) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        readyToReceive = false;
        payed = true;
        notifyAll();
    }

	/**
	 * Manager's method. Receives payment from customer.
	 */
	@Override
    public synchronized void receivePayment() {
        readyToReceive = true;
        notifyAll();
        while (!payed) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        payed = false;
    }

	/**
	 * Manager's method. Manager chooses the customer with the highest
	 * priority.
	 * 
	 * @return an Integer indicating the next customer's id to attend
	 */
	@Override
    public synchronized int currentCustomer() {
        if (customersQueue.isEmpty()) {
            return replacementQueue.peek();
        } else {
            return customersQueue.peek();
        }
    }

	/**
	 * Customer's method. The customer waits for a replacement car's key and 
	 * goes get one if it is available. Eventually, if the customer's car is 
	 * repaired while waiting, he goes to the normal queue.
	 */
	@Override
    public synchronized void collectKey() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);
		replacementQueue.add(((Customer) Thread.currentThread()).getCustomerId());
        notify();
        while (customerGetRepCar != ((Customer) Thread.currentThread()).getCustomerId() && !carsRepaired.contains(((Customer) Thread.currentThread()).getCustomerId())) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        if (carsRepaired.contains(((Customer) Thread.currentThread()).getCustomerId())) {
            carsRepaired.remove(((Customer) Thread.currentThread()).getCustomerId());
            replacementQueue.remove(((Customer) Thread.currentThread()).getCustomerId());
            ((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
            ((Customer) Thread.currentThread()).carRepaired = true;
        }
    }

	/**
	 * Manager's method. Manager waits while there is nothing to do.
	 */
	@Override
    public synchronized void getNextTask() {
        while (customersQueue.isEmpty() && customersToCallQueue.isEmpty() && piecesQueue.isEmpty()) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
    }

	/**
	 * Manager's method. Manager changes state to check what to do next.
	 */
	@Override
    public synchronized void checkWhatToDo() {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
    }

	/**
	 * Manager's method. Manager goes into the queue of customers to call and
	 * retrieves the head of the queue.
	 * 
	 * @return an Integer indicating the customer's id
	 */
	@Override
    public synchronized int getIdToCall() {
        return customersToCallQueue.poll();
    }

	/**
	 * Manager's method. When there is work to do, the manager chooses the task
	 * with the highest priority and changes state, accordingly.
	 */
	@Override
    public synchronized void appraiseSit() {
        if (!piecesQueue.isEmpty()) {
            ((Manager) Thread.currentThread()).setManagerState(ManagerState.GETTING_NEW_PARTS);
        } else if (!customersToCallQueue.isEmpty()) {
            ((Manager) Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
        } else if (!customersQueue.isEmpty()) {
            ((Manager) Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        }
    }

    /**
     * Mechanic's method. The mechanic alerts the manager that the car is 
	 * repaired or that he needs a type of a piece re stocked in the repair
	 * area.
     *
     * @param piece piece that needs to be re stocked
	 * @param customerId customer that needs to be called because his car is ready to be picked up
     */
    @Override
    public synchronized void alertManager(Piece piece, int customerId) {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        if (piece == null) {
            customersToCallQueue.add(customerId);
            notifyAll();
        } else {
            piecesQueue.add(piece);
            notifyAll();
        }
    }

    /**
     * Manager's method. The manager retrieves the piece that needs to be re
	 * stocked in the repair area.
	 * 
     * @return a piece that needs to be re stocked in the repair area
     */
    @Override
    public synchronized Piece getPieceToReStock() {
        return piecesQueue.poll();
    }

    /**
     * Manager's method. Manager changes state to go replenish stock.
     */
    @Override
    public synchronized void goReplenishStock() {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
    }

    /**
     * Manager's method. Manager checks if there is any work left to do.
	 * 
     * @return a boolean that represents if there is any work left to do
     */
    @Override
    public synchronized boolean enoughWork() {
		return customersQueue.isEmpty() && replacementQueue.isEmpty() && enoughWork;
    }

    /**
     * Manager's method. Manager checks if the customer is in outside world
	 * or in lounge and then calls customer and tells him that his car
	 * is ready to be picked up.
     *
     * @param id customer's id to call
     * @return a boolean that returns true if customer is in lounge and false if he's in outside world
     */
    @Override
    public synchronized boolean alertCustomer(int id) {
        if (replacementQueue.contains(id)) {
            carsRepaired.add(id);
            notifyAll();
            customersToCallQueue.remove(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method used for log. Retrieves the size of the queue to talk to the 
	 * manager.
	 * 
     * @return an Integer 
     */
    public int getCustomersQueueSize() {
        return customersQueue.size();
    }

    /**
     * Method used for log. Retrieves the size of the queue waiting for 
	 * replacement cars.
	 * 
     * @return an Integer that represents the size of the queue for replacement cars
     */
    public int getCustomersReplacementQueueSize() {
        return replacementQueue.size();
    }

    /**
     * Method used for log. Retrieves the number of the cars already repaired.
	 * 
     * @return an Integer that represents the number of the cars repaired
     */
    public int getCarsRepairedSize() {
        return carsRepaired.size();
    }

    /**
     * Method used for log. Retrieves which type of piece is missing.
	 * 
     * @return an array of booleans that is false if the manager was alerted
	 * that that piece is missing
	 * 
     */
    public boolean[] getFlagPartMissing() {
        return flagPartMissing;
    }
}
