/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import entities.Customer;
import entities.CustomerState;
import entities.Mechanic;
import entities.MechanicState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author andre and joao
 */
public class Park implements ICustomerP, IMechanicP, IManagerP {

    private int parkingSlots = 50;

    private final List<Integer> carsParked = new ArrayList<>();
    private final Queue<Integer> replacementCars = new LinkedList<>();
    private final HashMap<Integer, Integer> reserve = new HashMap<>();

    public Park(int ncars) {
        for (int i = 1; i < ncars + 1; i++) {
            replacementCars.add(i);
        }
        //carsParked.add(0);
        /*for (int i = 1; i < nCustomers; i++) { //start to List 
			carsParked.add(nCustomers+i);
		}*/
    }

    @Override
    public synchronized void parkCar(int id) {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
        //System.out.println("Customer " + ((Customer)Thread.currentThread()).getCustomerId() + " - Car parked.");
        carsParked.add(id);

        //System.out.println("---> Customer cars parked: " + carsParked.toString());
        parkingSlots--;
    }

    @Override
    public synchronized void collectCar(int id) {
        carsParked.remove(new Integer(id));
        parkingSlots++;
        //System.out.println("Customer cars: " + carsParked.toString());
    }

    @Override
    public synchronized int findCar() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.PARK);
        //System.out.println("REPLACEMENT CARS: " + replacementCars.toString());
        if (reserve.containsKey(((Customer) Thread.currentThread()).getCustomerId())) {
            int n = reserve.get(((Customer) Thread.currentThread()).getCustomerId());
            reserve.remove(((Customer) Thread.currentThread()).getCustomerId());
            replacementCars.remove(n);
            //System.out.println("----> Retrieved car " + n + " from REPLACEMENT CARS: " + replacementCars.toString());
            notifyAll();
            return n;
        } else {
            return 0;
        }
        /*
		nReplacementCars--;
		parkingSlots++;
		return carsParked.remove(nCustomers+1);
         */
    }

    @Override
    public synchronized void backToWorkByCar() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
    }

    /**
     * Mechanic's method. Mechanic goes into the park and gets the vehicle to
     * repair.
     *
     */
    @Override
    public synchronized void getVehicle(int id) {
        carsParked.remove(new Integer(id));
        parkingSlots++;
    }

    @Override
    public synchronized void returnReplacementCar(int id) {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
        replacementCars.add(id);
        //System.out.println("----> Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - Replacement car " + id + " parked.");
        //System.out.println("----> Replacement cars: " + replacementCars.toString());
    }

    /**
     * Mechanic's method. Mechanic goes into the park and park the already
     * repaired vehicle.
     *
     */
    @Override
    public synchronized void returnVehicle(int id) {
        //((Mechanic) Thread.currentThread()).setMechanicState(MechanicState.ALERTING_MANAGER);
        carsParked.add(id);
        parkingSlots--;
    }

    public int getParkingSlots() {
        return this.parkingSlots;
    }

    @Override
    public synchronized boolean getReplacementCar() {
        //System.out.println("------> " + replacementCars.toString() + " <------");
        if (replacementCars.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public synchronized void reserveCar(int id) {
        reserve.put(id, replacementCars.peek());
        System.out.println(reserve.toString());
    }

    @Override
    public synchronized void waitForCustomer(int id) {
        while (reserve.containsKey(id)) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
    }
	
	public int getCarsParkedSize(){
		return carsParked.size();
	}
	
	public int getReplacementCarsSize(){
		return replacementCars.size();
	}
}
