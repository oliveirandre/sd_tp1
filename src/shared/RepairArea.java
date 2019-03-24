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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private boolean workMechanic = false; //manager tem que alterar no post
	private int nRequestsManager = 0;
    private int mechanicToWork = 0;
    private boolean enoughWork = false;
	
    static final int nPieces = (int) (Math.random() * 13) + 3; //between 3 and 15 Math.random() * ((max - min) + 1)) + min; //0;

    private static final HashMap<EnumPiece, Integer> stock = new HashMap<>();

    public RepairArea(int nTypePieces) {

        for (int i = 0; i < nTypePieces; i++) {
            stock.put(EnumPiece.values()[i], 0); // 5
        }
		/*
		//adds random pieces to stock
        for (int i = 0; i < nPieces; i++) {
            Piece pec = new Piece();
            stock.put(pec.getTypePiece(), stock.get(pec.getTypePiece()) + 1);
        }*/

    }

	@Override
    public HashMap getPieces() {
        return stock;
    }

    private synchronized boolean pieceInStock(Piece p) {
        return stock.get(p.getTypePiece()) > 0;
    }

    private synchronized void removePieceFromStock(Piece p, int temp) {
		//int temp = stock.get(p.getTypePiece());
        
		stock.put(p.getTypePiece(), temp-1);
    }

    private synchronized void addPieceToStock(Piece p,int peido) {
		int temp = stock.get(p.getTypePiece());
		//System.out.println("TEMPTEMPTEMP:"+peido+temp);
        stock.put(p.getTypePiece(), peido+1);
    }

    /**
     * Mechanic's method. Reads the paper while there is no work. When a new car
     * is added to the queue, he exits this method.
     *
     */
    @Override
    public synchronized boolean readThePaper() {
		//System.out.println("Mechanic " + ((Mechanic) Thread.currentThread()).getId()+ " - Waiting for work...");
        //if(carsToRepair.isEmpty() || pieceToBeRepaired.isEmpty())
		//	workMechanic = false;
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
        int id = ((Mechanic)Thread.currentThread()).getMechanicId();
        /*if(mechanicsQueue.isEmpty())
            mechanicToWork = id;*/
        mechanicsQueue.add(id);
        //System.out.println("BEFORE - " + mechanicsQueue.toString());
        //while (!workMechanic) { //while there is no car to repair
        // readyToRepair.isEmpty() && 
        //mechanicToWork != id && 
        // && !workMechanic
        while(readyToRepair.isEmpty() && carsToRepair.isEmpty() && !enoughWork) {
            //if(mechanicToWork != id) {
                try {
                    wait();
                } catch (Exception e) {

                }
            //}
		}
        //workMechanic = false;
        //System.out.println("WORK MECHANIC " + workMechanic);
        //workMechanic = false;
        if(enoughWork)
            return true;
        //System.out.println("CARS TO REPAIR " + carsToRepair.toString());
        //System.out.println("READY TO REPAIR " + readyToRepair.toString());
        //System.out.println(mechanicToWork + " to fix car " + carsToRepair.peek() + " or " + readyToRepair.peek());
        //System.out.println("mechanicToWork : ME - " + id + " | CALLED - " +  mechanicToWork);
        //if(!readyToRepair.isEmpty())
            //System.out.println("Mechanic " + mechanicToWork + " to fix car " + readyToRepair.peek() + " - READY TO REPAIR");
        //else
            //System.out.println("Mechanic " + mechanicToWork + " to fix car " + carsToRepair.peek() + " - CARS TO REPAIR");
            
        //repair.add(carsToRepair.poll());
        mechanicsQueue.poll();
        //System.out.println("carsToRepair " + (carsToRepair.isEmpty()));
        //System.out.println("workMechanic " + workMechanic);
        //System.out.println("AFTER - " + mechanicsQueue.toString());
        return false;
    }

    /**
     * Mechanic's method. Change the state to start to fix the car.
     *
     * @return idCar
     */
    @Override
    public synchronized int startRepairProcedure() {
        //System.out.println("Mechanic - Starting repair procedure");
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.FIXING_CAR);
		//System.out.println(carsToRepair);
		
        //System.out.println(carsToRepair.toString());
        if(readyToRepair.isEmpty() && carsToRepair.isEmpty()) {
            ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
            return 0;
        }
        else if(!readyToRepair.isEmpty()) {
            //System.out.println("Going to repair car " + readyToRepair.peek());
            return readyToRepair.poll();
        }
        else {
            //System.out.println("Going to repair car " + carsToRepair.peek());
			return carsToRepair.poll();
		}
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
		//System.out.println("mecanico a reparar carro, ou seja, a retirar peça do stock");
        repaired.add(id);
		removePieceFromStock(piece, stock.get(piece.getTypePiece()));
		
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
        //System.out.println("CAR - " + id);
        piecesToBeRepaired.put(id, new Piece());
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
    public synchronized void letManagerKnow(Piece piece, int idCustomerNeedsPiece) {
        //System.out.println("Mechanic - Letting manager know");
        ((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
        carsWaitingForPieces.put(idCustomerNeedsPiece, piece);
        carsToRepair.remove(idCustomerNeedsPiece);
        //notifyAll();
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
        //if(!readyToRepair.contains(idCustomer) && !carsWaitingForPieces.containsKey(idCustomer) && !repaired.contains(idCustomer)) {
        if(!alreadyAdded.contains(idCustomer)) {
            //System.out.println("Added customer "+ idCustomer + "registerService");
            carsToRepair.add(idCustomer);
        }
        alreadyAdded.add(idCustomer);
		//System.out.println(idCustomer);
        workMechanic = true;
        if(!mechanicsQueue.isEmpty()) {
            mechanicToWork = mechanicsQueue.peek();
            notify();
        }
        /*else
            mechanicToWork = 0;*/
        //mechanicsQueue.poll();
		//System.out.println(carsToRepair);
        
        //mechanicToWork = 0;
		nRequestsManager++;
    }

    @Override
    public synchronized int storePart(Piece part, int quant) {
        int n = 0;
        for(int j = 0; j < carsWaitingForPieces.size(); j++) {
            Piece p = carsWaitingForPieces.get(carsWaitingForPieces.keySet().toArray()[j]);
            if(p == part)
                n = (int) getKeyFromValue(carsWaitingForPieces, p);          
        }
        readyToRepair.add(n);
        carsWaitingForPieces.remove(n);
        //System.out.println(quant + " " + part.getTypePiece() + " ADDED FOR CAR " + readyToRepair.toString());
        for (int i = 0; i < quant; i++) {
			
		}
		addPieceToStock(part,stock.get(part.getTypePiece()));
		//System.out.println("STOCK ATUALIZADO:: " + stock);
        /*HashMap.Entry<Integer,Piece> entry = carsWaitingForPieces.entrySet().iterator().next();
        Integer key = entry.getKey();
        Piece value = entry.getValue();*/
		return n;
    }
	
    public Object getKeyFromValue(HashMap hm, Object value) {
        for (Object o : hm.keySet()) {
          if (hm.get(o).equals(value)) {
            return o;
          }
        }
        return null;
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
		/*for (Piece value : piecesToBeRepaired.values()) {
			if(value.getIdTypePiece()==i)
				nVehiclesWaitingForParts[i]++;
			i++;
		}*/

		return nVehiclesWaitingForParts;
	}
    
    @Override
    public synchronized void enoughWork() {
        //((Manager)Thread.currentThread()).setManagerState(null);
        //System.out.println("ENOUGH");
        enoughWork = true;
        notifyAll();
    }
}
