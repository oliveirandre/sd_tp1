package repository;

import entities.CustomerState;
import entities.ManagerState;
import entities.MechanicState;
import genclass.GenericIO;
import genclass.TextFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author andre e joao
 */
public class RepairShop {
	private int count=0;

    private int nMechanics;
    private int nCustomers;
	private int nTypePieces;
	private MechanicState[] mechanicsStates;
	private CustomerState[] customersStates;	
	private ManagerState managerState;
	
    private String fileName = "repairShop.log";
	
	
	//lounge updates
	private Queue<Integer> replacementQueue = new LinkedList<>();
    private Queue<Integer> customersQueue = new LinkedList<>();
	private boolean[] flagPartMissing;
	private Queue<Integer> carsRepaired = new LinkedList<>();
    private boolean[] requiresReplacementCar;
	//park updates
	private List<Integer> carsParked = new ArrayList<>();
    private Queue<Integer> replacementCars = new LinkedList<>();
	//supplierSite updates
	private int[] piecesBought;
	//repairArea updates
	private int nRequestsManager;
	private HashMap<Integer, Piece> piecesToBeRepaired = new HashMap<>();
	private HashMap<EnumPiece, Integer> stock = new HashMap<>();
	//outsideWorld
	private String[] vehicleDriven;
	
	
	
	/**
	 * RepairShop's constructor. This is where everything is initialized and
	 * the log start.
	 * @param nMechanics number of mechanics
	 * @param nCustomers number of customers
	 * @param fileName log file name
	 */
	public RepairShop(int nTypePieces, int nMechanics, int nCustomers, String fileName) {
        this.nTypePieces = nTypePieces;
		this.nMechanics = nMechanics;
		this.nCustomers = nCustomers;
		mechanicsStates = new MechanicState[nMechanics];
		customersStates = new CustomerState[nCustomers];
		requiresReplacementCar = new boolean[nCustomers];
		piecesBought = new int[nTypePieces];
		vehicleDriven = new String[nCustomers];
		flagPartMissing = new boolean[nTypePieces];
        
        for (int i = 0; i < nTypePieces; i++) {
            piecesBought[i] = 0;
            stock.put(EnumPiece.values()[i], 0);
            flagPartMissing[i] = true;
        }
		
		
		for (int i = 0; i < nMechanics; i++) {
			mechanicsStates[i] = MechanicState.values()[0];
		}
		
		for (int i = 0; i < nCustomers; i++) {
			customersStates[i] = CustomerState.values()[0];
			requiresReplacementCar[i] = false;
			if(i<10) vehicleDriven[i] = "0"+Integer.toString(i);
			else vehicleDriven[i] = Integer.toString(i);
		}
		managerState = ManagerState.values()[0];
		
        if ((fileName != null) && !("".equals(fileName))) {
            this.fileName = fileName;
        }
		
    }
	
	public synchronized void updateFromLounge(Queue<Integer> replacementQueue, Queue<Integer> customersQueue, Queue<Integer> carsRepaired, boolean[] requiresReplacementCar){
		this.replacementQueue = replacementQueue;
		this.customersQueue = customersQueue;
		this.carsRepaired = carsRepaired;
        this.requiresReplacementCar = requiresReplacementCar;
		reportStatus();
	}
	
	public synchronized void updateFromLounge(Queue<Integer> replacementQueue, Queue<Integer> customersQueue, Queue<Integer> carsRepaired, boolean[] requiresReplacementCar, int idCustomer, CustomerState state){
		customersStates[idCustomer] = state;
		updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar);
	}
	
	public synchronized void updateFromLounge(ManagerState state){
		managerState = state;
		updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar);
	}
	
    public synchronized void updateFromLounge(Queue<Integer> replacementQueue, Queue<Integer> customersQueue, Queue<Integer> carsRepaired, boolean[] requiresReplacementCar, int idMechanic, MechanicState state){
		mechanicsStates[idMechanic] = state;
		updateFromLounge(replacementQueue, customersQueue, carsRepaired, requiresReplacementCar);
	}
    
	public synchronized void updateFromPark(List<Integer> carsParked, Queue<Integer> replacementCars){
		this.carsParked = carsParked;
		this.replacementCars = replacementCars;
		reportStatus();
	}
	
	public synchronized void updateFromPark(List<Integer> carsParked, Queue<Integer> replacementCars, int idCustomer, CustomerState state){
		customersStates[idCustomer] = state;
		updateFromPark(carsParked, replacementCars);
	}
    
    public synchronized void updateFromPark(List<Integer> carsParked, Queue<Integer> replacementCars, int idMechanic, MechanicState state){
		mechanicsStates[idMechanic] = state;
		updateFromPark(carsParked, replacementCars);
	}
	
	public synchronized void updateFromOutsideWorld(String[] vehicleDriven){
		this.vehicleDriven = vehicleDriven;
		reportStatus();
	}
	
	public synchronized void updateFromOutsideWorld(int idCustomer, CustomerState state){
		customersStates[idCustomer] = state;
		updateFromOutsideWorld(vehicleDriven);
	}
	
	public synchronized void updateFromSupplierSite(int[] piecesBought){
		this.piecesBought = piecesBought;
		reportStatus();
	}
	
	public synchronized void updateFromRepairArea(int nRequestsManager, HashMap<Integer, Piece> piecesToBeRepaired, boolean[] flagPartMissing, HashMap<EnumPiece, Integer> stock){
		this.nRequestsManager = nRequestsManager;
		this.piecesToBeRepaired = piecesToBeRepaired;
		this.flagPartMissing = flagPartMissing;
		this.stock = stock;
		reportStatus();
	}
	
	public synchronized void updateFromRepairArea(int nRequestsManager, HashMap<Integer, Piece> piecesToBeRepaired, boolean[] flagPartMissing, HashMap<EnumPiece, Integer> stock, ManagerState state){
		managerState = state;
		updateFromRepairArea(nRequestsManager, piecesToBeRepaired, flagPartMissing, stock);
	}
	
	public synchronized void updateFromRepairArea(int nRequestsManager, HashMap<Integer, Piece> piecesToBeRepaired, boolean[] flagPartMissing, HashMap<EnumPiece, Integer> stock, int idMechanic, MechanicState state){
		mechanicsStates[idMechanic] = state;
		updateFromRepairArea(nRequestsManager, piecesToBeRepaired, flagPartMissing, stock);
	}
	
	
	private int[] getNumberVehiclesWaitingForParts(int nTypePieces) {
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
	
    public void reportInitialStatus() {
        TextFile log = new TextFile(); // instanciaÃ§Ã£o de uma variÃ¡vel de tipo ficheiro de texto

        //fileName = "repairShop.log";
        if (!log.openForWriting("./src", fileName)) {
            GenericIO.writelnString("A operação de criação do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }
        log.writelnString("                                       REPAIR SHOP ACTIVITIES - Description of the internal state of the problem");

        if (!log.close()) {
            GenericIO.writelnString("A operaçã de fecho do ficheiro " + fileName + " falhou! -initialstatus");
            System.exit(1);
        }
        reportStatus();
    }

	/**
	 * Method to update log in every state change.
	 */
	private void reportStatus() {
        
		TextFile log = new TextFile(); // instanciação de uma variável de tipo ficheiro de texto

        String lineStatus = ""; // linha a imprimir

        if (!log.openForAppending("./src", fileName)) {
            GenericIO.writelnString("A operação de criação do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }
        lineStatus += " MAN  MECHANIC                                                                  CUSTOMER\n";
        //( (Customer) Thread.currentThread() ).carRepaired
        lineStatus += managerState.toString() + "  ";

        for (int i = 0; i < nMechanics; i++) {
            lineStatus += mechanicsStates[i].toString() + " ";
        }
        //Manager states: aCtm, cwtd, gnwp, pjob, alrC, repS
        //Mechanic stats: wfw, ftc, amg, cks
        //Customer stats: lwc, prk, wrc, rcp, lnc

        //S00 ### customer state 
        //C00 ## vehicle driven by customer: own car – customer id; replacement car – R0, R1, R2 ; none - ‘--’ (# - 0 .. 29)
        //P00 # (requires replacementCar; T or F)
        //R00 #  if it has already been repaired (T or F)
        lineStatus += " ";
		//String[] customersState = new String[nCustomers];
        //String[] vehicleDriven = new String[nCustomers];
		String[] requiresReplacement = new String[nCustomers];
		String[] repaired = new String[nCustomers];
		
		
		for (int i = 0; i < nCustomers; i++) {
			if(carsRepaired.contains(i))
				repaired[i] = "T";
			else repaired[i] = "F";
            
            ///VEHICLE DRIVEN REQUIREMENT
            
            if(requiresReplacementCar[i])
                requiresReplacement[i]="T";
            else requiresReplacement[i]="F";
		}
		
        
		
        int t = 20;
        for (int j = 0; j < 30; j += 10) {
            for (int i = j; i < nCustomers - t; i++) {
                lineStatus += customersStates[i] + "  "
                        + vehicleDriven[i] + "  "
                        + requiresReplacement[i] + "   "
                        + repaired[i] + "  ";
            }
            t -= 10;
            lineStatus += "\n               ";
        }
        //lineStatus += "\n";
        //LOUNGE
        String InQ = "";
        String WtK = "";
        String NRV = "";

        //PARK
        String NCV = ""; //NCV - number of customer vehicles presently parked at the repair shop park
        String NPV = ""; //NPV - number of replacement vehicles presently parked at the repair shop park

        //REPAIR AREA 
        String NSRQ = ""; //NSRQ – number of service requests made by the manager to the repair area

        lineStatus += "    LOUNGE        PARK                             REPAIR AREA"
                + "                                           SUPPLIER SITE\n";

        if (customersQueue.size() < 10) {
            InQ = "0" + Integer.toString(customersQueue.size());
        } else {
            InQ = Integer.toString(customersQueue.size());
        }

        if (replacementQueue.size() < 10) {
            WtK = "0" + Integer.toString(replacementQueue.size());
        } else {
            WtK = Integer.toString(replacementQueue.size());
        }

        if (carsRepaired.size() < 10) {
            NRV = "0" + Integer.toString(carsRepaired.size());
        } else {
            NRV = Integer.toString(carsRepaired.size());
        }

        if (carsParked.size() < 10) {
            NCV = "0" + Integer.toString(carsParked.size());
        } else {
            NCV = Integer.toString(carsParked.size());
        }

        if (replacementCars.size() < 10) {
            NPV = "0" + Integer.toString(replacementCars.size());
        } else {
            NPV = Integer.toString(replacementCars.size());
        }

        if (nRequestsManager < 10) {
            NSRQ = "0" + Integer.toString(nRequestsManager);
        } else {
            NSRQ = Integer.toString(nRequestsManager);
        }

        lineStatus += "                 "
                + InQ + "  "
                + WtK + "  "
                + NRV + "     "
                + NCV + "   "
                + NPV + "        "
                + NSRQ + "     ";

        //REPAIR AREA 
        String[] Prt = new String[nTypePieces]; //Prt# - number of parts of type # presently in storage at the repair area (# - 0 .. 2)
        String[] NV = new String[nTypePieces]; //NV# - number of customer vehicles waiting for part # to be available so that the repair may resume (# - 0 .. 2)
        String[] S = new String[nTypePieces]; //S# - flag signaling the manager has been adviced that part # is missing at the repair area: T or F (# - 0 .. 2)

        //SUPPLIER SITE
        String[] PP = new String[nTypePieces]; //PP# - number of parts of type # which have been purchased so far by the manager (# - 0 .. 2)
        //## ## # ## ## # ## ## #

        for (int i = 0; i < nTypePieces; i++) {
            Object[] temp = stock.values().toArray();
            if ((int) temp[i] < 10) {
                Prt[i] = "0" + Integer.toString((int) temp[i]);
            } else {
                Prt[i] = "" + Integer.toString((int) temp[i]);
            }

            if (getNumberVehiclesWaitingForParts(nTypePieces)[i] < 10) {
                NV[i] = "0" + getNumberVehiclesWaitingForParts(nTypePieces)[i];
            } else {
                NV[i] = "" + getNumberVehiclesWaitingForParts(nTypePieces)[i];
            }

            if (flagPartMissing[i]) {
                S[i] = "T";
            } else {
                S[i] = "F";
            }

            if (piecesBought[i] < 10) {
                PP[i] = "0" + piecesBought[i];
            } else {
                PP[i] = "" + piecesBought[i];
            }

            lineStatus += Prt[i] + "    "
                    + NV[i] + "   "
                    + S[i] + "  ";
        }

        lineStatus += "                        ";
        for (int i = 0; i < nTypePieces; i++) {
            lineStatus += PP[i] + "   ";
        }

        //if (!lineStatus.toLowerCase().contains("null")) {
        //    log.writelnString(lineStatus);
        //}
		//System.out.println(count++);
		log.writelnString(lineStatus);
		
        if (!log.close()) {
            GenericIO.writelnString("A operação de fecho do ficheiro " + fileName + " falhou!");
            System.exit(1);
        }
    }

}
