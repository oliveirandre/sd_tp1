package repository;

import entities.Customer;
import entities.Manager;
import entities.Mechanic;
import genclass.GenericIO;
import genclass.TextFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ICustomerL;
import shared.ICustomerOW;
import shared.ICustomerP;
import shared.IManagerL;
import shared.IManagerOW;
import shared.IManagerP;
import shared.IManagerRA;
import shared.IManagerSS;
import shared.IMechanicL;
import shared.IMechanicP;
import shared.IMechanicRA;
import shared.Lounge;
import shared.OutsideWorld;
import shared.Park;
import shared.RepairArea;
import shared.SupplierSite;

/**
 *
 * @author andre e joao
 */
public class RepairShop {
	
	private int nMechanics;
	private int nCustomers;
	private int nReplacementCars;

	private Customer customers[];
	private Mechanic mechanics[];
	private Manager manager;

	//shared
	private Lounge lounge;
	private Park park;
	private OutsideWorld outsideWorld;
	private RepairArea repairArea;
	private SupplierSite supplierSite;

	private String fileName = "repairShop.log";

	public RepairShop(int nMechanics, int nCustomers, int nReplacementCars, String fileName) {
		/* fixar os parâmetros do problema */
		this.nReplacementCars = nReplacementCars;
		this.nCustomers = nCustomers;
		this.nMechanics = nMechanics;
		mechanics = new Mechanic[nMechanics];
		customers = new Customer[nCustomers];     // array de threads cliente
		
		
		
		final int nTypePieces = 3;
		
		//runnable entities
		//shared entities
		lounge = new Lounge(nTypePieces);
		outsideWorld = new OutsideWorld();
		park = new Park(nReplacementCars);
		repairArea = new RepairArea(nTypePieces);
		supplierSite = new SupplierSite(nTypePieces);

		for (int i = 0; i < nCustomers; i++) {
			customers[i] = new Customer((ICustomerOW) outsideWorld, (ICustomerP) park, (ICustomerL) lounge, i + 1, this);
		}

		for (int i = 0; i < nMechanics; i++) {
			mechanics[i] = new Mechanic((IMechanicP) park, (IMechanicRA) repairArea, (IMechanicL) lounge, i + 1, this);
		}

		manager = new Manager((IManagerL) lounge, (IManagerRA) repairArea, (IManagerSS) supplierSite, (IManagerOW) outsideWorld, (IManagerP) park, nCustomers, this);

		manager.start();

		for (int i = 0; i < nMechanics; i++) {
			mechanics[i].start();
		}

		for (int i = 0; i < nCustomers; i++) {
			customers[i].start();
		}

		if ((fileName != null) && !("".equals(fileName))) {
			this.fileName = fileName;
		}
		reportInitialStatus();
		
		
		for (int j = 0; j < nMechanics; j++) {
			try {
				mechanics[j].join();
				System.err.println("Mechanic " + j + " Died ");
			} catch (InterruptedException ex) {
				//Escrever para o log
			}
		}
		
		try {
			manager.join();
			System.err.println("Manager Died ");
            //System.err.println("----------");
		} catch (InterruptedException ex) {
			//Escrever para o log
		}
		
		
		for (int j = 0; j < nCustomers; j++) {
			try {
				customers[j].join();
				System.err.println("Customer " + j + " Died ");
			} catch (InterruptedException ex) {
				//Escrever para o log
			}
		}
		
	}
	
	
	private void reportInitialStatus() {
		TextFile log = new TextFile(); // instanciaÃ§Ã£o de uma variÃ¡vel de tipo ficheiro de texto

		//fileName = "repairShop.log";

		if (!log.openForWriting(".", fileName)) {
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
	
	public synchronized void reportStatus() {
		TextFile log = new TextFile(); // instanciação de uma variável de tipo ficheiro de texto

		String lineStatus = ""; // linha a imprimir
		
		if (!log.openForAppending(".", fileName)) {
			GenericIO.writelnString("A operação de criação do ficheiro " + fileName + " falhou!");
			System.exit(1);
		}
		lineStatus += " MAN  MECHANIC                                                                  CUSTOMER\n";
		//( (Customer) Thread.currentThread() ).carRepaired
		lineStatus += manager.getManagerState() + "  ";

		for (int i = 0; i < nMechanics; i++) {
			lineStatus += mechanics[i].getMechanicState() + " ";
				/*switch (mechanics[i].getMechanicState().toString()) {
				case "wfw":
					lineStatus += " wfw";
					break;

				case "ftc":
					lineStatus += " ftc";
					break;

				case "amg":
					lineStatus += " amg";
					break;

				case "cks":
					lineStatus += " cks";
					break;

			}*/
		}
		//Manager states: aCtm, cwtd, gnwp, pjob, alrC, repS
		//Mechanic stats: wfw, ftc, amg, cks
		//Customer stats: lwc, prk, wrc, rcp, lnc

		//S00 ### customer state 
		//C00 ## vehicle driven by customer: own car – customer id; replacement car – R0, R1, R2 ; none - ‘-’ (# - 0 .. 29)
		//P00 # (requires replacementCar; T or F)
		//R00 #  if it has already been repaired (T or F)
		lineStatus += " ";
		
		String[] C = new String [nCustomers];
		
		int t = 20;
		for (int j = 0; j < 30; j+=10) {
			for (int i = j; i < nCustomers - t; i++) {
				lineStatus += customers[i].getCustomerState() + "  "
					+ customers[i].getCustomerVehicle() + "  "
					+ customers[i].requiresReplacementCar() + "   "
					+ customers[i].vehicleRepaired() + "  ";
			}
			t-=10;
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

		if (lounge.getCustomersQueueSize() < 10) {
			InQ = "0" + Integer.toString(lounge.getCustomersQueueSize());
		} else {
			InQ = Integer.toString(lounge.getCustomersQueueSize());
		}

		if (lounge.getCustomersReplacementQueueSize() < 10) {
			WtK = "0" + Integer.toString(lounge.getCustomersReplacementQueueSize());
		} else {
			WtK = Integer.toString(lounge.getCustomersReplacementQueueSize());
		}

		if (lounge.getCarsRepairedSize() < 10) {
			NRV = "0" + Integer.toString(lounge.getCarsRepairedSize());
		} else {
			NRV = Integer.toString(lounge.getCarsRepairedSize());
		}

		if (park.getCarsParkedSize() < 10) {
			NCV = "0" + Integer.toString(park.getCarsParkedSize());
		} else {
			NCV = Integer.toString(park.getCarsParkedSize());
		}

		if (park.getReplacementCarsSize() < 10) {
			NPV = "0" + Integer.toString(park.getReplacementCarsSize());
		} else {
			NPV = Integer.toString(park.getReplacementCarsSize());
		}

		if (repairArea.getRequestsManagerSize() < 10) {
			NSRQ = "0" + Integer.toString(repairArea.getRequestsManagerSize());
		} else {
			NSRQ = Integer.toString(repairArea.getRequestsManagerSize());
		}

		lineStatus += "                 "
			+ InQ + "  "
			+ WtK + "  "
			+ NRV + "     "
			+ NCV + "   "
			+ NPV + "        "
			+ NSRQ + "     ";

		//REPAIR AREA 
		int nTypePieces = 3;
		String[] Prt = new String[nTypePieces]; //Prt# - number of parts of type # presently in storage at the repair area (# - 0 .. 2)
		String[] NV = new String[nTypePieces]; //NV# - number of customer vehicles waiting for part # to be available so that the repair may resume (# - 0 .. 2)
		String[] S = new String[nTypePieces]; //S# - flag signaling the manager has been adviced that part # is missing at the repair area: T or F (# - 0 .. 2)

		//SUPPLIER SITE
		String[] PP = new String[nTypePieces]; //PP# - number of parts of type # which have been purchased so far by the manager (# - 0 .. 2)
		//## ## # ## ## # ## ## #

		//ALTERAR AQUI COM ARGUMENTO A ENTRAR nTypePieces
		for (int i = 0; i < nTypePieces; i++) {
			Object[] temp = repairArea.getPieces().values().toArray();
			if ((int)temp[i] < 10) {
				Prt[i] = "0" + Integer.toString((int)temp[i]);
			} else {
				Prt[i] = "" + Integer.toString((int)temp[i]);
			}

			if (repairArea.getNumberVehiclesWaitingForParts(nTypePieces)[i] < 10) {
				NV[i] = "0" + repairArea.getNumberVehiclesWaitingForParts(nTypePieces)[i];
			} else {
				NV[i] = "" + repairArea.getNumberVehiclesWaitingForParts(nTypePieces)[i];
			}

			if (lounge.getFlagPartMissing()[i]) {
				S[i] = "T";
			} else {
				S[i] = "F";
			}

			if (supplierSite.getPiecesBought()[i] < 10) {
				PP[i] = "0" + supplierSite.getPiecesBought()[i];
			} else {
				PP[i] = "" + supplierSite.getPiecesBought()[i];
			}

			lineStatus += Prt[i] + "    "
				+ NV[i] + "   "
				+ S[i] + "  ";
		}

		lineStatus += "                        ";
		for (int i = 0; i < nTypePieces; i++) {
			lineStatus += PP[i] + "   ";
		}
		
		if(!lineStatus.toLowerCase().contains("null"))
			log.writelnString(lineStatus);
		if (!log.close()) {
			GenericIO.writelnString("A operação de fecho do ficheiro " + fileName + " falhou!");
			System.exit(1);
		}

	}

}
