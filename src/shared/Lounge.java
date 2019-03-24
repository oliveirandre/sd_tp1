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
    private Piece pieceToReStock2;
    private boolean managerAvailable = false;
    private boolean ordered = false;
    private boolean payed = false;
    private final boolean enoughWork = false;
    private final Queue<Integer> customersToCallQueue = new LinkedList<>();
    private final Queue<Integer> carsRepaired = new LinkedList<>();
    private final boolean[] flagPartMissing;
    private boolean readyToReceive;

    private static HashMap<Integer, String> order = new HashMap<Integer, String>();

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
	 * Customer's method. 
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
	 *
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
     * Mechanic's method.
     *
     * @param piece
     * @param idCar
     */
    @Override
    public synchronized void alertManager(Piece piece, int idCar) {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        if (piece == null) {
            customersToCallQueue.add(idCar);
            notifyAll();
        } else {
            piecesQueue.add(piece);
            notifyAll();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public synchronized Piece getPieceToReStock() {
        return piecesQueue.poll();
    }

    /**
     *
     */
    @Override
    public synchronized void goReplenishStock() {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
    }

    /**
     *
     * @return
     */
    @Override
    public synchronized boolean enoughWork() {
        if (customersQueue.isEmpty() && replacementQueue.isEmpty() && enoughWork) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * DO MANAGER PARA CUSTOMER
     *
     * @param id
     * @return
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
     *
     * @return
     */
    public int getCustomersQueueSize() {
        return customersQueue.size();
    }

    /**
     *
     * @return
     */
    public int getCustomersReplacementQueueSize() {
        return replacementQueue.size();
    }

    /**
     *
     * @return
     */
    public int getCarsRepairedSize() {
        return carsRepaired.size();
    }

    /**
     *
     * @return
     */
    public int getPieceMissingId() {
        return pieceToReStock2.getTypePiece().ordinal();
    }

    /**
     *
     * @return
     */
    public boolean[] getFlagPartMissing() {
        return flagPartMissing;
    }
}
