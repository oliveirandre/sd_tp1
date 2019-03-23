package main;

/**
 *
 * @author Andre e Joao
 */
import entities.Customer;
import entities.Manager;
import entities.Mechanic;
import genclass.GenericIO;
import genclass.TextFile;
import repository.EnumPiece;
import repository.Log;
import repository.Log2;
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

public class Main {

	private static Lounge lounge;
	private static OutsideWorld outsideWorld;
	private static Park park;
	private static RepairArea repairArea;
	private static SupplierSite supplierSite;

	private static Log2 log;
	private static Manager manager;
	private static Mechanic mechanics[];
	private static Customer customers[];

	public static void main(String[] args) {
		/* 
         * There are 5 different locations: park, lounge, repair area, supplier
         * site and the outside world.
         * We assume that there are 30 customers, 2 mechanics and 1 manager.
         * Furthermore, there are 3 replacement vehicles and 3 distinct parts 
         * that can be repared.
		 */

		final int nCustomers = 10;
		final int nMechanics = 1;
		final int nManagers = 1;
		final int nReplacementCars = 3;
		final int nTypePieces = 3;

		log = Log2.getInstance();

		lounge = new Lounge();
		outsideWorld = new OutsideWorld();
		park = new Park(nReplacementCars);
		repairArea = new RepairArea(nTypePieces);
		supplierSite = new SupplierSite(nTypePieces);

		// initialization of threads
		for (int i = 0; i < nManagers; i++) {
			manager = new Manager((IManagerL) lounge, (IManagerRA) repairArea, (IManagerSS) supplierSite, (IManagerOW) outsideWorld, (IManagerP) park);
			manager.start();
		}

		customers = new Customer[nCustomers];
		for (int i = 0; i < nCustomers; i++) {
			customers[i] = new Customer((ICustomerOW) outsideWorld, (ICustomerP) park, (ICustomerL) lounge, i + 1);
			customers[i].start();
		}

		mechanics = new Mechanic[nMechanics];
		for (int i = 0; i < nMechanics; i++) {
			mechanics[i] = new Mechanic((IMechanicP) park, (IMechanicRA) repairArea, (IMechanicL) lounge, i + 1);
			mechanics[i].start();
		}
		
		
		reportInitialStatus(nMechanics, nCustomers);

		for (int j = 0; j < nManagers; j++) {
			try {
				manager.join();
				System.err.println("Manager " + j + " Died ");
			} catch (InterruptedException ex) {
				//Escrever para o log
			}
		}

		for (int j = 0; j < nMechanics; j++) {
			try {
				mechanics[j].join();
				System.err.println("Mechanic " + j + " Died ");
			} catch (InterruptedException ex) {
				//Escrever para o log
			}
		}
	}

	public static void reportInitialStatus(int nMechanics, int nCustomers) {
		TextFile log = new TextFile(); // instanciaÃ§Ã£o de uma variÃ¡vel de tipo ficheiro de texto

		String fileName = "Project.log";

		if (!log.openForWriting(".", fileName)) {
			GenericIO.writelnString("A operação de criação do ficheiro " + fileName + " falhou!");
			System.exit(1);
		}
		log.writelnString("                                       REPAIR SHOP ACTIVITIES - Description of the internal state of the problem");
		log.writelnString();
		log.writelnString(" MAN  MECHANIC                                                                  CUSTOMER");

		if (!log.close()) {
			GenericIO.writelnString("A operaçã de fecho do ficheiro " + fileName + " falhou!");
			System.exit(1);
		}
		reportStatus(fileName, nMechanics, nCustomers);
	}

	/**
	 * Escrever o estado actual (operaÃ§Ã£o interna).
	 * <p>
	 * Uma linha de texto com o estado de actividade dos barbeiros e dos
	 * clientes Ã© escrito no ficheiro.
	 */
	public static void reportStatus(String fileName, int nMechanics, int nCustomers) {
		TextFile log = new TextFile(); // instanciação de uma variável de tipo ficheiro de texto

		String lineStatus = ""; // linha a imprimir

		//( (Customer) Thread.currentThread() ).carRepaired
		if (!log.openForAppending(".", fileName)) {
			GenericIO.writelnString("A operação de criação do ficheiro " + fileName + " falhou!");
			System.exit(1);
		}

		lineStatus += manager.getManagerState() + " ";

		for (int i = 0; i < nMechanics; i++) {
			switch (mechanics[i].getMechanicState().toString()) {
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

			}
		}
		//Manager states: aCtm, cwtd, gnwp, pjob, alrC, repS
		//Mechanic stats: wfw, ftc, amg, cks
		//Customer stats: lwc, prk, wrc, rcp, lnc

		//S00 ### customer state 
		//C00 ## vehicle driven by customer: own car – customer id; replacement car – R0, R1, R2 ; none - ‘-’ (# - 0 .. 29)
		//P00 # (requires replacementCar; T or F)
		//R00 #  if it has already been repaired (T or F)
		
		lineStatus += "  ";
		
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < nCustomers/3; i++) {
				lineStatus += customers[i].getCustomerState().toString() + "  " 
							+ customers[i].getCustomerVehicle() + "  " 
							+ customers[i].requiresReplacementCar() + "   " 
							+ customers[i].vehicleRepaired() + "  ";
			}
			lineStatus += "\n               ";
		}
		
		//LOUNGE
		String InQ="";
		String WtK ="";
		String NRV="";
		
		//PARK
		String NCV=""; //NCV - number of customer vehicles presently parked at the repair shop park
		String NPV=""; //NPV - number of replacement vehicles presently parked at the repair shop park
		
		//REPAIR AREA 
		String NSRQ=""; //NSRQ – number of service requests made by the manager to the repair area
		
		
		
		
		lineStatus += "\n                   LOUNGE        PARK                             REPAIR AREA"
					+ "                                           SUPPLIERS SITE\n";
		
		if(lounge.getCustomersQueueSize()<10)
			InQ = "0"+Integer.toString(lounge.getCustomersQueueSize());
		else InQ = Integer.toString(lounge.getCustomersQueueSize());
		
		if(lounge.getCustomersReplacementQueueSize()<10)
			WtK = "0"+Integer.toString(lounge.getCustomersReplacementQueueSize());
		else WtK = Integer.toString(lounge.getCustomersReplacementQueueSize());
		
		if(lounge.getCarsRepairedSize()<10)
			NRV = "0"+Integer.toString(lounge.getCarsRepairedSize());
		else NRV = Integer.toString(lounge.getCarsRepairedSize());
		
		
		if(park.getCarsParkedSize()<10)
			NCV = "0"+Integer.toString(park.getCarsParkedSize());
		else NCV = Integer.toString(park.getCarsParkedSize());
		
		if(park.getReplacementCarsSize()<10)
			NPV = "0"+Integer.toString(park.getReplacementCarsSize());
		else NPV = Integer.toString(park.getReplacementCarsSize());
		
		
		if(repairArea.getRequestsManagerSize()<10)
			NSRQ = "0"+Integer.toString(repairArea.getRequestsManagerSize());
		else NSRQ = Integer.toString(repairArea.getRequestsManagerSize());
			
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
			int temp = (int) repairArea.getPieces().get(EnumPiece.values()[i]);
			if(temp<10)
				Prt[i]=""+Integer.toString(temp);
			else Prt[i]="0"+Integer.toString(temp);
			
			//System.out.println("PEIDO GROSSO"+lounge.getPieceToReStock().getIdTypePiece());
			//aqui calcula-se os valores NV e S
			//if(i==lounge.getPieceMissingId())
			//	S[i]="T";
			//else S[i]="F";
			
			if(supplierSite.getPiecesBought()[i]<10)
				PP[i] = "0"+supplierSite.getPiecesBought()[i];
			else PP[i] = ""+supplierSite.getPiecesBought()[i];
			
			lineStatus += Prt[i] + "    "
						+ NV[i] + "   "
						+ S[i] + "  ";
		}
		
		lineStatus += "                        ";
		for (int i = 0; i < nTypePieces; i++) {
			lineStatus += PP[i] + "   ";
		}
		
		
		
		
			
		log.writelnString(lineStatus);
		if (!log.close()) {
			GenericIO.writelnString("A operação de fecho do ficheiro " + fileName + " falhou!");
			System.exit(1);
		}

	}
	
	
}
