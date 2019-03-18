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
import entities.Mechanic;
import repository.Piece;
import repository.RepairShop;
import shared.ICustomerL;
import shared.ICustomerOW;
import shared.ICustomerP;
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
    
    private static RepairShop repairShop;
    
    public static void main(String[] args) {
        /* 
         * There are 5 different locations: park, lounge, repair area, supplier
         * site and the outside world.
         * We assume that there are 30 customers, 2 mechanics and 1 manager.
         * Furthermore, there are 3 replacement vehicles and 3 distinct parts 
         * that can be repared.
         */
		
		int N_OF_REPLACEMENT_CARS = 3;
        
        repairShop = new RepairShop();
        
        int nCustomers = repairShop.N_OF_CUSTOMERS;
        int nMechanics = repairShop.N_OF_MECHANICS;
        int nManagers = repairShop.N_OF_MANAGERS;
        
        lounge = new Lounge();
        outsideWorld = new OutsideWorld();
        park = new Park(repairShop.N_OF_REPLACEMENT_CARS);
        repairArea = new RepairArea();
        supplierSite = new SupplierSite();
		
        // initialization of threads
        for(int i = 0; i < nCustomers; i++) {
            Customer c = new Customer((ICustomerOW) outsideWorld, (ICustomerP) park, (ICustomerL) lounge, i);
            //c.start();
        }
        
        for(int i = 0; i < nMechanics; i++) {
            Mechanic mec = new Mechanic((IMechanicP) park, (IMechanicRA) repairArea, (IMechanicL) lounge, i, (RepairShop) repairShop);
			mec.start();
        }
        
        for(int i = 0; i < nManagers; i++) {
            
        }
		
        Piece p = new Piece();
		
		RepairShop repair = new RepairShop();
		
        System.out.println(p.getTypePiece());
		System.out.println(RepairShop.getPieces());
		
		
		System.out.println(RepairShop.getPieces());
		
		
    }
    
}

