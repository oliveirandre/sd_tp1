package shared;

import entities.Customer;
import entities.CustomerState;
import entities.Manager;
import entities.ManagerState;
import entities.MechanicState;
import java.util.Arrays;
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
    
    private boolean readyToReceive;
	private RepairShop repairShop;
    private boolean[] requiresReplacementCar;

    private static HashMap<Integer, String> order = new HashMap<Integer, String>();

	/**
	 *
	 * @param nTypePieces
	 */
	public Lounge(int nCustomers, int nTypePieces, RepairShop repairShop) {
        requiresReplacementCar = new boolean[nCustomers];
        
        Arrays.fill(requiresReplacementCar, false);
        
		this.repairShop = repairShop;
    }

    /**
     * Customer's method. After parking the car in need of a repair, the
     * customer now has to wait in a queue to be attended by the manager.
     *
     * @param id customer's id
     */
    @Override
    public synchronized void queueIn(int id, CustomerState state) {
        customersQueue.add(id);
		repairShop.updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar, id, state);
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
	 * @param carRepaired
	 * @param requiresCar
     */
    @Override
    public synchronized void talkWithManager(boolean carRepaired, boolean requiresCar) {
        if (carRepaired) {
            order.put(nextCustomer, "pay");
        } else {
            if (requiresCar) {
                order.put(nextCustomer, "car");
                requiresReplacementCar[nextCustomer] = true;
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
		repairShop.updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar);
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
        requiresReplacementCar[nextCustomer] = false;
		repairShop.updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar);
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
     * @param state
	 * @return an Integer indicating the next customer's id to attend
	 */
	@Override
    public synchronized int currentCustomer(ManagerState state) {
        repairShop.updateFromLounge(state);
        int next = 0;
        if (customersQueue.isEmpty()) {
            next = replacementQueue.peek();
        } else {
            next = customersQueue.peek();
        }
        //MANDAR PARA LOG
        return next;
    }

	/**
	 * Customer's method. The customer waits for a replacement car's key and 
	 * goes get one if it is available. Eventually, if the customer's car is 
	 * repaired while waiting, he goes to the normal queue.
	 * 
	 * @param id
	 * @return 
	 */
	@Override
    public synchronized boolean collectKey(int id, CustomerState state) {
        //((Customer) Thread.currentThread()).setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);
		replacementQueue.add(id);
		//repairShop.updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar);
        repairShop.updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar, id, state);
        notify();
        while (customerGetRepCar != id && !carsRepaired.contains(id)) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        if (carsRepaired.contains(id)) {
            carsRepaired.remove(id);
            replacementQueue.remove(id);
            requiresReplacementCar[nextCustomer] = false;
            //((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
            //((Customer) Thread.currentThread()).carRepaired = true;
			repairShop.updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar);
            return true;
        }
		else
            return false;
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
    public synchronized void checkWhatToDo(ManagerState state) {
        repairShop.updateFromLounge(state); //MANDAR PARA LOG
        //((Manager) Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
    }

	/**
	 * Manager's method. Manager goes into the queue of customers to call and
	 * retrieves the head of the queue.
	 * 
	 * @return an Integer indicating the customer's id
	 */
	@Override
    public synchronized int getIdToCall(ManagerState state) {
        repairShop.updateFromLounge(state); //MANDAR PARA LOG
        int next = customersToCallQueue.poll();
        return next;
    }

	/**
	 * Manager's method. When there is work to do, the manager chooses the task
	 * with the highest priority and changes state, accordingly.
	 */
	@Override
    public synchronized int appraiseSit() {
        if (!piecesQueue.isEmpty()) {
            return 1;
            //((Manager) Thread.currentThread()).setManagerState(ManagerState.GETTING_NEW_PARTS);
        } else if (!customersToCallQueue.isEmpty()) {
            return 2;
            //((Manager) Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
        } else if (!customersQueue.isEmpty()) {
            return 3;
            //((Manager) Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        }
        else {
            return 0;
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
    public synchronized void alertManager(Piece piece, int customerId, int idMechanic, MechanicState state) {
        repairShop.updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar, idMechanic, state);
        //((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
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
    public synchronized Piece getPieceToReStock(ManagerState state) {
        Piece p = piecesQueue.poll();
		repairShop.updateFromLounge(state);//MANDAR PARA LOG
        return p;
    }

    /**
     * Manager's method. Manager changes state to go replenish stock.
     */
    @Override
    public synchronized void goReplenishStock(ManagerState state) {
        //((Manager) Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
        repairShop.updateFromLounge(state);//MANDAR PARA LOG
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
			repairShop.updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar);
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
     *//*
    public boolean[] getFlagPartMissing() {
        return flagPartMissing;
    }*/
}
