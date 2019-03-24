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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import repository.EnumPiece;
import repository.Log;
import repository.Log2;
import repository.RepairShop;
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

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		/* 
         * There are 5 different locations: park, lounge, repair area, supplier
         * site and the outside world.
         * We assume that there are 30 customers, 2 mechanics and 1 manager.
         * Furthermore, there are 3 replacement vehicles and 3 distinct parts 
         * that can be repared.
		 */
		
		final int nCustomers = 30;
		final int nMechanics = 2;
		final int nManagers = 1;
		final int nReplacementCars = 3;
		final int nTypePieces = 3;
		String fileName = "repairShop.log";
		RepairShop repairShop;
		
		repairShop = new RepairShop(nMechanics, nCustomers, nReplacementCars, fileName);
		
		
		
		/*final int nCustomers = 29;
		final int nMechanics = 2;
		final int nManagers = 1;
		final int nReplacementCars = 3;
		final int nTypePieces = 3;

		log = Log2.getInstance();

		lounge = new Lounge(nTypePieces);
		outsideWorld = new OutsideWorld();
		park = new Park(nReplacementCars);
		repairArea = new RepairArea(nTypePieces);
		supplierSite = new SupplierSite(nTypePieces);

		// initialization of threads
		for (int i = 0; i < nManagers; i++) {
			manager = new Manager((IManagerL) lounge, (IManagerRA) repairArea, (IManagerSS) supplierSite, (IManagerOW) outsideWorld, (IManagerP) park, nCustomers);
			manager.start();
		}

		mechanics = new Mechanic[nMechanics];
		for (int i = 0; i < nMechanics; i++) {
			mechanics[i] = new Mechanic((IMechanicP) park, (IMechanicRA) repairArea, (IMechanicL) lounge, i + 1);
			mechanics[i].start();
		}

		customers = new Customer[nCustomers];
		for (int i = 0; i < nCustomers; i++) {
			customers[i] = new Customer((ICustomerOW) outsideWorld, (ICustomerP) park, (ICustomerL) lounge, i + 1);
			customers[i].start();
		}
		
		
		//reportInitialStatus(nMechanics, nCustomers);

		for (int j = 0; j < nManagers; j++) {
			try {
				manager.join();
				System.err.println("Manager " + j + " Died ");
                //System.err.println("----------");
			} catch (InterruptedException ex) {
				//Escrever para o log
			}
		}
		
		for (int j = 0; j < nCustomers; j++) {
			try {
				customers[j].join();
				System.err.println("Customer " + j + " Died ");
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
		}*/
		
		
	}
}
