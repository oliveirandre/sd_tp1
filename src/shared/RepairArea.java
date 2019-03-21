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
    private HashMap<Integer, Piece> pieceToBeRepaired = new HashMap<>();
    private boolean work = false; //manager tem que alterar no post
	private int idCustomer;
	
	
	static final int nPieces = (int) (Math.random() * 13) + 3; //between 3 and 15 Math.random() * ((max - min) + 1)) + min


	private final static HashMap<EnumPiece, Integer> stock = new HashMap<>();
	
	
	
	public RepairArea(int N_OF_TYPE_PIECES){
		
		for (int i = 0; i < N_OF_TYPE_PIECES; i++) {
			stock.put(EnumPiece.values()[i], 0); 
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
	 * Mechanic's method. Reads the paper while there is no work.
	 * When a new car is added to the queue, he exits this method.
	 *
	 */
	@Override
	public synchronized void readThePaper() {
		if(pieceToBeRepaired.isEmpty() || carsToRepair.isEmpty())
			work = false;
        //System.out.println("Mechanic - Waiting for work...");
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        while (!work) { //while there is no car to repair
            try {
                wait();
                if(work) {
                    return;
                }
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
		
		 
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
        return carsToRepair.poll();
		/*try{
			
		}catch(Exception e){
			System.out.println("PILAS" + carsToRepair);
		}
		return ;*/
    }

	/**
	 * Mechanic's method. Mechanic removes one piece from stock
	 * to repair the car in question
	 * 
	 * 
	 * @param id
	 * @param piece
	 */
	@Override
    public synchronized void fixIt(int id, Piece piece) {
		//System.out.println("Mechanic - Fixing it");
        removePieceFromStock(piece);
        pieceToBeRepaired.remove(id, piece);
    }

	/**
	 * Mechanic's method. Mechanic checks the car and finds what piece it needs.
	 * If no required piece is associated to a car, it creates a new one. 
	 * 
	 * @param id
	 * @return the HashMap containing the cars with the required piece, respectively
	 */
	@Override
    public synchronized HashMap getRequiredPart(int id) {
		//System.out.println("Mechanic - Getting required part");
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.CHECKING_STOCK);
        pieceToBeRepaired.putIfAbsent(id, new Piece());
        return pieceToBeRepaired;
    }

	/**
	 * Mechanic's method. After receiving a new car to fix, checks if the required
	 * piece is available in stock.
	 * 
	 * @param part
	 * @return returns true if the piece is available and false otherwise
	 */
	@Override
    public boolean partAvailable(Piece part) {
        return pieceInStock(part);
    }
	
	
    @Override
    public synchronized void letManagerKnow() {
		//System.out.println("Mechanic - Letting manager know");
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
        notify();
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
	 * Mechanic's method. Change the state to alert the manager that the
	 * repair is concluded.
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

	@Override
	public synchronized void registerService(int idCustomer) {
		((Manager) Thread.currentThread()).setManagerState(ManagerState.POSTING_JOB);
		carsToRepair.add(idCustomer);
		work = true;
		System.out.println("MECANICO, PÕE TE A TRABALHAR");
		notify();
	}

	@Override
	public synchronized int getIdFromManager() {
		return idCustomer;
	}

	@Override
	public synchronized void storePart(Piece part) {
		addPieceToStock(part);
	}
    
}
