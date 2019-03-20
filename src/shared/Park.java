/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import entities.Customer;
import entities.CustomerState;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andre and joao
 */
public class Park implements ICustomerP, IMechanicP {

    private int parkingSlots = 50;
    private int nReplacementCars = 3;
	private final int nCustomers;
	
    private List<Integer> carsParked = new ArrayList<Integer>();
	
	public Park(int nCustomers){
		this.nCustomers = nCustomers;
		for (int i = 1; i < nCustomers; i++) { //start to List 
			carsParked.add(nCustomers+i);
		}
	}
	
    @Override
    public synchronized void parkCar(int id) {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
        System.out.println("Customer " + ((Customer)Thread.currentThread()).getCustomerId() + " - Car parked.");
        carsParked.add(id);
        parkingSlots--;
    }
    
    @Override
    public synchronized void collectCar(int id) {
        carsParked.remove(id);
        parkingSlots++;
    }
    
    @Override
    public synchronized int findCar() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.PARK);
		nReplacementCars--;
		parkingSlots++;
		return carsParked.remove(nCustomers+1);
    }

    @Override
    public synchronized void backToWorkByCar() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
    }
	
	/**
	 * Mechanic's method. Mechanic goes into the park and gets the vehicle 
	 * to repair.
	 *  
	 */
    @Override
    public synchronized void getVehicle(int id) {
        carsParked.remove(id);
		parkingSlots++;
    }
	
	/**
	 * Mechanic's method. Mechanic goes into the park and park the already
	 * repaired vehicle.
	 *  
	 */
    @Override
    public synchronized void returnVehicle(int id) {
        carsParked.add(id);
		parkingSlots--;
    }

    public int getParkingSlots() {
        return this.parkingSlots;
    }

}