/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Andre e Joao
 */
import entities.Customer;
import entities.Manager;
import entities.Mechanic;
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

        final int N_OF_CUSTOMERS = 5;
        final int N_OF_MECHANICS = 1;
        final int N_OF_MANAGERS = 1;
        final int N_OF_REPLACEMENT_CARS = 3;
        int N_OF_TYPE_PIECES = 3;

        int nCustomers = N_OF_CUSTOMERS;
        int nMechanics = N_OF_MECHANICS;
        int nManagers = N_OF_MANAGERS;
		
		log = Log.getInstance();

		
        lounge = new Lounge();
        outsideWorld = new OutsideWorld();
        park = new Park(N_OF_REPLACEMENT_CARS);
        repairArea = new RepairArea(N_OF_TYPE_PIECES);
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
        
		
		for(int j = 0; j < nManagers; j++){
            try {
                manager.join();
                System.err.println("Manager " + j + " Died ");
            } catch (InterruptedException ex) {
                //Escrever para o log
            }
        }
        
        for(int j = 0; j < nMechanics; j++){
            try {
                mechanics[j].join();
                System.err.println("Mechanic " + j + " Died ");
            } catch (InterruptedException ex) {
                //Escrever para o log
            }
        }
		

    }

}
