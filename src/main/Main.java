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
import repository.Log;
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

	private static Log log;
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

		final int nCustomers = 4;
		final int nMechanics = 2;
		final int nManagers = 1;
		final int nReplacementCars = 3;
		final int nTypePieces = 3;


		log = Log.getInstance();

		lounge = new Lounge();
		outsideWorld = new OutsideWorld();
		park = new Park(nReplacementCars);
		repairArea = new RepairArea(nTypePieces);
		supplierSite = new SupplierSite();

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
			mechanics[i] = new Mechanic((IMechanicP) park, (IMechanicRA) repairArea, (IMechanicL) lounge, i);
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
		/*
		for (int i = 0; i < nMechanics; i++) {
			switch (mechanics[i].getMechanicState().) {
				case wfw:
					lineStatus += " DORMINDO ";
					break;
				
				case ftc:
					lineStatus += " ACTIVIDA ";
					break;
				
				case amg:
					lineStatus += " ACTIVIDA ";
					break;
				
				case cks:
					lineStatus += " ACTIVIDA ";
					break;
				
			}
		}
		*/
		/*
		for (int i = 0; i < nCustomer; i++) {
			switch (stateCustomer[i]) {
				case LIVNORML:
					lineStatus += " VIVVNRML ";
					break;
				case WANTCUTH:
					lineStatus += " QUERCORT ";
					break;
				case WAITTURN:
					lineStatus += " ESPERAVZ ";
					break;
				case CUTHAIR:
					lineStatus += " CORTACBL ";
					break;
			}
		}*/
		
		log.writelnString(lineStatus);
		if (!log.close()) {
			GenericIO.writelnString("A operação de fecho do ficheiro " + fileName + " falhou!");
			System.exit(1);
		}

	}
}
