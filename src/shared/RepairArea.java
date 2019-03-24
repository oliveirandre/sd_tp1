package shared;

import entities.Manager;
import entities.ManagerState;
import entities.Mechanic;
import entities.MechanicState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import repository.EnumPiece;
import repository.Piece;

/**
 *
 * @author andre and joao
 */
public class RepairArea implements IMechanicRA, IManagerRA {

    private final Queue<Integer> carsToRepair = new LinkedList<>();
    private final HashMap<Integer, Piece> carsWaitingForPieces = new HashMap<>();
    private final Queue<Integer> readyToRepair = new LinkedList<>();
    private final Queue<Integer> repaired = new LinkedList<>();
    private final HashMap<Integer, Piece> piecesToBeRepaired = new HashMap<>();
    private final List<Integer> alreadyAdded = new ArrayList<>();
    private final Queue<Integer> mechanicsQueue = new LinkedList<>();
    private boolean workMechanic = false;
    private int nRequestsManager = 0;
    private boolean enoughWork = false;

    static final int nPieces = (int) (Math.random() * 13) + 3; //between 3 and 15 Math.random() * ((max - min) + 1)) + min; //0;

    private static final HashMap<EnumPiece, Integer> stock = new HashMap<>();

	/**
	 * RepairArea's constructor. Initializes the stock and adds random pieces 
	 * to stock.
	 * @param nTypePieces
	 */
	public RepairArea(int nTypePieces) {

        for (int i = 0; i < nTypePieces; i++) {
            stock.put(EnumPiece.values()[i], 0);
        }

        //adds random pieces to stock
        for (int i = 0; i < nPieces; i++) {
            Piece pec = new Piece();
            stock.put(pec.getTypePiece(), stock.get(pec.getTypePiece()) + 1);
        }

    }

    /**
     * Mechanic's method. Returns the current stock in Repair Area.
     *
     * @return HashMap stock of pieces in Repair Area
     */
    @Override
    public HashMap getPieces() {
        return stock;
    }

    private boolean pieceInStock(Piece p) {
        return stock.get(p.getTypePiece()) > 0;
    }

    private void removePieceFromStock(Piece p) {
        stock.put(p.getTypePiece(), stock.get(p.getTypePiece()) - 1);
    }

    private void addPieceToStock(Piece p) {
        stock.put(p.getTypePiece(), stock.get(p.getTypePiece()) + 1);
    }

    /**
     * Mechanic's method. Reads the paper while there is no work. When a he is
     * alerted by the manager, he starts to work.
     *
	 * @return a boolean representing if mechanic has more work or can go home
     */
    @Override
    public synchronized boolean readThePaper() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        int id = ((Mechanic) Thread.currentThread()).getMechanicId();
        mechanicsQueue.add(id);
        while (readyToRepair.isEmpty() && carsToRepair.isEmpty() && !enoughWork) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
        if (enoughWork) {
            return true;
        }
        mechanicsQueue.poll();
        return false;
    }

    /**
     * Mechanic's method. Change the state to start to fix the car.
     *
     * @return a Integer representing the id of the car to be checked/repaired
     * next
     */
    @Override
    public synchronized int startRepairProcedure() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
        if (readyToRepair.isEmpty() && carsToRepair.isEmpty()) {
            ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
            return 0;
        } else if (!readyToRepair.isEmpty()) {
            return readyToRepair.poll();
        } else {
            return carsToRepair.poll();
        }
    }

    /**
     * Mechanic's method. Mechanic removes one piece from stock to repair the
     * car in question
     *
     *
     * @param id the id of the car that is going to get repaired
     * @param piece the piece that car needs to be repaired
     */
    @Override
    public synchronized void fixIt(int id, Piece piece) {
        repaired.add(id);
        if (stock.get(piece.getTypePiece()) == 0) {
            ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
            return;
        }
        removePieceFromStock(piece);
        piecesToBeRepaired.remove(id, piece);
    }

    /**
     * Mechanic's method. Mechanic checks the car and finds what piece it needs.
     *
     * @param id the car it needs to be checked
     */
    @Override
    public synchronized void getRequiredPart(int id) {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.CHECKING_STOCK);
        piecesToBeRepaired.put(id, new Piece());
    }

    /**
     * Mechanic's method. After receiving a new car to fix, checks if the
     * required piece is available in stock.
     *
     * @param part a piece
     * @return returns true if the piece is available and false otherwise
     */
    @Override
    public synchronized boolean partAvailable(Piece part) {
        return pieceInStock(part);
    }

    /**
     * Mechanic's method. Removes the car from the queue CarsToRepair and adds
     * it to the CarsWaitingForPieces. Changes state to alert the manager if it
     * needs a new piece.
     *
     * @param piece piece that is required to fix the car
     * @param idCustomerNeedsPiece the id of the card that needs to be fixed
     * associated to this piece
     */
    @Override
    public synchronized void letManagerKnow(Piece piece, int idCustomerNeedsPiece) {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
        carsWaitingForPieces.put(idCustomerNeedsPiece, piece);
        carsToRepair.remove(idCustomerNeedsPiece);
    }

    /**
     * Mechanic's method. After knowing that the required part is available in
     * stock, the mechanic's state changes to fix the car.
     *
     */
    @Override
    public synchronized void resumeRepairProcedure() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
    }

    /**
     * Mechanic's method. Change the state to alert the manager that the repair
     * is concluded.
     *
     */
    @Override
    public synchronized void repairConcluded() {
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
    }

    /**
     * Manager's method. Changes state to check what to do next.
     */
    @Override
    public synchronized void getNextTask() {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
    }

    /**
     * Manager's method. After receiving a request from a customer, the manager
     * registers it for further use by the mechanics.
     *
     * @param idCustomer the id of the car that the mechanic needs to repair
     */
    @Override
    public synchronized void registerService(int idCustomer) {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.POSTING_JOB);
        if (!alreadyAdded.contains(idCustomer)) {
            carsToRepair.add(idCustomer);
        }
        alreadyAdded.add(idCustomer);
        if (!mechanicsQueue.isEmpty()) {
            notify();
        }
        nRequestsManager++;
    }

    /**
     * Manager's method. The manager comes from supplier site with a type of
     * piece and its quantity, and stores them in Repair Area.
     *
     * @param part a type of piece
     * @param quant the quantity of the piece that is going to be added to stock
     * @return a Integer representing the id of the car that needed this type of
     * piece
     */
    @Override
    public synchronized int storePart(Piece part, int quant) {
        int n = 0;
        for (int j = 0; j < carsWaitingForPieces.size(); j++) {
            Piece p = carsWaitingForPieces.get(carsWaitingForPieces.keySet().toArray()[j]);
            if (p == part) {
                n = (int) getKeyFromValue(carsWaitingForPieces, p);
            }
        }
        readyToRepair.add(n);
        carsWaitingForPieces.remove(n);
        for (int i = 0; i < quant; i++) {
			addPieceToStock(part);
        }
        return n;
    }

    private Object getKeyFromValue(HashMap hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    /**
     * Mechanic's method. Returns the pieces that are needed to be repair the
     * cars.
     *
     * @return a HashMap that contains the pieces that are needed to be repair
     * the cars
     */
    @Override
    public synchronized HashMap getPiecesToBeRepaired() {
        return piecesToBeRepaired;
    }

    /**
     * Method used for log. Returns the number of requests made by the manager.
     *
     * @return a Integer representing the number of requests made by the manager
     */
    public int getRequestsManagerSize() {
        return nRequestsManager;
    }

    /**
     * Method used for log. Returns the number of vehicles waiting for each
     * piece.
     *
     * @param nTypePieces the number of different types of pieces
     * @return an Array with the number of vehicles waiting for each piece
     */
    public int[] getNumberVehiclesWaitingForParts(int nTypePieces) {
        int[] nVehiclesWaitingForParts = new int[nTypePieces];

        for (int i = 0; i < nTypePieces; i++) {
            nVehiclesWaitingForParts[i] = 0;
        }
        
		for (int j = 0; j < nTypePieces; j++) {
			for (Piece value : piecesToBeRepaired.values()) {
				if (value.getIdTypePiece() == j) {
					nVehiclesWaitingForParts[j]++;
				}
			}
		}
        return nVehiclesWaitingForParts;
    }

    /**
     * Manager's method. Notifies mechanics that work is done for the day.
     */
    @Override
    public synchronized void enoughWork() {
        enoughWork = true;
        notifyAll();
    }
}
