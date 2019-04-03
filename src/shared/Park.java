package shared;

import entities.Customer;
import entities.CustomerState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import repository.RepairShop;

/**
 *
 * @author andre and joao
 */
public class Park implements ICustomerP, IMechanicP, IManagerP {

    private int parkingSlots = 50;
	private RepairShop repairShop;
	
    private final List<Integer> carsParked = new ArrayList<>();
    private final Queue<Integer> replacementCars = new LinkedList<>();
    private final HashMap<Integer, Integer> reserve = new HashMap<>();

    /**
     * Park Constructor. Initializes the replacement cars in the park.
     *
     * @param nReplacementCars the number of replacement cars
     */
    public Park(int nReplacementCars, RepairShop repairShop) {
		this.repairShop = repairShop;
        for (int i = 1; i < nReplacementCars + 1; i++) {
            replacementCars.add(i);
        }
		repairShop.updateFromPark(carsParked, replacementCars);
    }

    /**
     * Customer's method. Customer arrives to Repair Shop and parks is car in
     * the park.
     *
     * @param id the id of the car
     */
    @Override
    public synchronized void parkCar(int id, CustomerState state) {
        //((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
        carsParked.add(id);
        parkingSlots--;
		repairShop.updateFromPark(carsParked, replacementCars, id, state);
    }

    /**
     * Customer's method. Customer goes into the park and collect his own car.
     *
     * @param id the id of the car
     */
    @Override
    public synchronized void collectCar(int id) {
        carsParked.remove(new Integer(id));
        parkingSlots++;
		repairShop.updateFromPark(carsParked, replacementCars);
    }

    /**
     * Customer's method. Customer goes into the park and finds the replacement
     * car that has been associated to him.
     *
     * @return an Integer representing the replacement car id
     */
    @Override
    public synchronized int findCar(int id, CustomerState state) {
        //((Customer) Thread.currentThread()).setCustomerState(CustomerState.PARK);
        if (reserve.containsKey(((Customer) Thread.currentThread()).getCustomerId())) {
            int n = reserve.get(((Customer) Thread.currentThread()).getCustomerId());
            reserve.remove(((Customer) Thread.currentThread()).getCustomerId());
            replacementCars.remove(n);
			repairShop.updateFromPark(carsParked, replacementCars, id, state);
            notifyAll();
            return n;
        } else {
			repairShop.updateFromPark(carsParked, replacementCars, id, state);
            return 0;
        }
    }

    /**
     * Mechanic's method. Mechanic goes into the park and gets the vehicle to
     * repair.
     *
     * @param id the car's id that is going to be checked in the repair area
     */
    @Override
    public synchronized void getVehicle(int id) {
        carsParked.remove(new Integer(id));
        parkingSlots++;
		repairShop.updateFromPark(carsParked, replacementCars);
    }

    /**
     * Customer's method. After being alerted that his own car is ready, the
     * customer goes into the park and parks his replacement car.
     *
     * @param id the replacement car's id
     */
    @Override
    public synchronized void returnReplacementCar(int id, int idCustomer, CustomerState state) {
        //((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
        replacementCars.add(id);
		repairShop.updateFromPark(carsParked, replacementCars, id, state);
    }

    /**
     * Mechanic's method. Mechanic goes into the park and park the already
     * repaired vehicle.
     *
     * @param id id of the vehicle that has been worked on
     */
    @Override
    public synchronized void returnVehicle(int id) {
        carsParked.add(id);
        parkingSlots--;
		repairShop.updateFromPark(carsParked, replacementCars);
    }

    /**
     * Method used for log. Retrieves the number of parking slots available.
     *
     * @return an Integer representing the number of parking slots available
     */
    public int getParkingSlots() {
        return this.parkingSlots;
    }

    /**
     * Manager's method. Manager checks if there is any replacement car
     * available in the park.
     *
     * @return a boolean representing if a replacement car is available
     */
    @Override
    public synchronized boolean replacementCarAvailable() {
        if (replacementCars.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Manager's method. Reserves a replacement car for a customer.
     *
     * @param id customer's id
     */
    @Override
    public synchronized void reserveCar(int id) {
        reserve.put(id, replacementCars.peek());
    }

    /**
     * Manager's method. Manager waits for customer to get the replacement car
     * it was given to him.
     *
     * @param id
     */
    @Override
    public synchronized void waitForCustomer(int id) {
        while (reserve.containsKey(id)) {
            try {
                wait();
            } catch (Exception e) {

            }
        }
    }

    /**
     * Method used for log. Retrieves the number of customer cars parked.
     *
     * @return an Integer that represents the number of customer cars parked
     */
    public int getCarsParkedSize() {
        return carsParked.size();
    }

    /**
     * Method used for log. Retrieves the number of replacement cars available
     *
     * @return an Integer that represents the number of replacement cars
     * available
     */
    public int getReplacementCarsSize() {
        return replacementCars.size();
    }
}
