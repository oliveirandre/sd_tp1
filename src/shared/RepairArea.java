package shared;

import entities.Manager;
import entities.ManagerState;
import entities.Mechanic;
import entities.MechanicState;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import repository.EnumPiece;
import repository.Piece;

/**
 *
 * @author andre and joao
 */
public class RepairArea implements IMechanicRA, IManagerRA {

    private final Queue<Integer> carsToRepair = new LinkedList<>();
    private Queue<Integer> carsWaitingForPieces = new LinkedList<>();
    private HashMap<Integer, Piece> piecesToBeRepaired = new HashMap<>();
    private boolean workMechanic = false; //manager tem que alterar no post
	private int idCustomerNeedsPiece;
	private boolean busyMechanic = true;
	private int nRequestsManager = 0;
	
    static final int nPieces = (int) (Math.random() * 13) + 3; //between 3 and 15 Math.random() * ((max - min) + 1)) + min

    private final static HashMap<EnumPiece, Integer> stock = new HashMap<>();

    public RepairArea(int nTypePieces) {

        for (int i = 0; i < nTypePieces; i++) {
            stock.put(EnumPiece.values()[i], 5);
        }

        for (int i = 0; i < nPieces; i++) {
            Piece pec = new Piece();
            stock.put(pec.getTypePiece(), stock.get(pec.getTypePiece()) + 1);
        }

    }

    public static HashMap getPieces() {
        return stock;
    }

    public boolean pieceInStock(Piece p) {
        return stock.get(p.getTypePiece()) != 0;
    }

    public void removePieceFromStock(Piece p) {
        stock.put(p.getTypePiece(), stock.get(p.getTypePiece()) - 1);
    }

    public void addPieceToStock(Piece p) {
        stock.put(p.getTypePiece(), stock.get(p.getTypePiece()) + 1);
    }

    /**
     * Mechanic's method. Reads the paper while there is no work. When a new car
     * is added to the queue, he exits this method.
     *
     */
    @Override
    public synchronized void readThePaper() {
		//System.out.println("Mechanic " + ((Mechanic) Thread.currentThread()).getId()+ " - Waiting for work...");
        //if(carsToRepair.isEmpty() || pieceToBeRepaired.isEmpty())
		//	workMechanic = false;
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        
        //while (!workMechanic) { //while there is no car to repair
        while(carsToRepair.isEmpty() && !workMechanic) {
            try {
                wait();
            } catch (Exception e) {
				
			}
		}
    }

    /**
     * Mechanic's method. Change the state to start to fix the car.
     *
     * @return idCar
     */
    @Override
    public synchronized int startRepairProcedure() {
        //System.out.println("Mechanic - Starting repair procedure");
        workMechanic = false;
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
		//System.out.println(carsToRepair);
		busyMechanic = false;
        return carsToRepair.poll();
    }

    /**
     * Mechanic's method. Mechanic removes one piece from stock to repair the
     * car in question
     *
     *
     * @param id
     * @param piece
     */
    @Override
    public synchronized void fixIt(int id, Piece piece) {
        removePieceFromStock(piece);
        piecesToBeRepaired.remove(id, piece);
    }

    /**
     * Mechanic's method. Mechanic checks the car and finds what piece it needs.
     * If no required piece is associated to a car, it creates a new one.
     *
     * @param id
     * @return the HashMap containing the cars with the required piece,
     * respectively
     */
    @Override
    public synchronized void getRequiredPart(int id) {
        //System.out.println("Mechanic - Getting required part");
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.CHECKING_STOCK);
        piecesToBeRepaired.putIfAbsent(id, new Piece());
    }

    /**
     * Mechanic's method. After receiving a new car to fix, checks if the
     * required piece is available in stock.
     *
     * @param part
     * @return returns true if the piece is available and false otherwise
     */
    @Override
    public boolean partAvailable(Piece part) {
        return pieceInStock(part);
    }

    @Override
    public synchronized void letManagerKnow(int idCustomerNeedsPiece) {
        //System.out.println("Mechanic - Letting manager know");
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
        notifyAll();
		this.idCustomerNeedsPiece = idCustomerNeedsPiece;
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
     * Mechanic's method. Change the state to alert the manager that the repair
     * is concluded.
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
	
	/*
    ** Manager's method. After receiving a request from a customer, the manager 
    ** registers it for further use by the mechanics.
     */
    @Override
    public synchronized void registerService(int idCustomer) {
        ((Manager) Thread.currentThread()).setManagerState(ManagerState.POSTING_JOB);
        carsToRepair.add(idCustomer);
		//System.out.println(idCustomer);
        workMechanic = true;
		//System.out.println(carsToRepair);
        notify();
		nRequestsManager++;
    }

    @Override
    public synchronized int storePart(Piece part, int quant) {
        for (int i = 1; i < quant; i++) {
			addPieceToStock(part);
		}
		return idCustomerNeedsPiece;
    }
	
	@Override
    public synchronized HashMap getPiecesToBeRepaired() {
        return piecesToBeRepaired;
    }
	
	public int getRequestsManagerSize(){
		return nRequestsManager;
	}
	
	public int[] getNumberVehiclesWaitingForParts(int nTypePieces) {
		//private HashMap<Integer, Piece> piecesToBeRepaired = new HashMap<>();
		//piecesToBeRepaired.putIfAbsent(id, newPart);
		// KEY, VALUE
		int[] nVehiclesWaitingForParts = new int[nTypePieces];

		for (int i = 0; i < nTypePieces; i++) {
			nVehiclesWaitingForParts[i] = 0;
		}
		int i = 0;
		for (Piece value : piecesToBeRepaired.values()) {
			if(value.getIdTypePiece()==i)
				nVehiclesWaitingForParts[i]++;
			i++;
		}

		return nVehiclesWaitingForParts;
	}
}
