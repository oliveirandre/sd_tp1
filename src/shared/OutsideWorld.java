package shared;

import entities.Customer;
import entities.CustomerState;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import repository.RepairShop;

/**
 *
 * @author andre and joao
 */
public class OutsideWorld implements ICustomerOW, IManagerOW {
	private final RepairShop repairShop;
    private final List<Integer> repairedCars;
    private final List<Integer> waitingForCar;
    private final String[] vehicleDriven;
	
	public OutsideWorld(int nCustomers, RepairShop repairShop){
        this.waitingForCar = new ArrayList<>();
        this.repairedCars = new ArrayList<>();
        vehicleDriven = new String[nCustomers];
		this.repairShop = repairShop;
		
		for (int i = 0; i < nCustomers; i++) {
			if(i<10) vehicleDriven[i] = "0"+Integer.toString(i);
			else vehicleDriven[i] = Integer.toString(i);
		}
	}
    /**
     * Customer's method. The customer starts his life span in the outside world
     * until he decides do repair his car. Furthermore, he also decides if he is
     * going to need a replacement car or not.
     */
    @Override
    public synchronized boolean decideOnRepair(int id, CustomerState state) {
		repairShop.updateFromOutsideWorld(id, state);
        Random requires = new Random();
        Random n = new Random();
        int randomNum = 0;
        while (randomNum != 1) {
            randomNum = n.nextInt((100 - 1) + 1) + 1;
        }
        return requires.nextBoolean();
    }

    /**
     * Customer's method. After going back to work by bus, the customer waits
     * for the manager to tell him that his car has been repaired.
	 * @param carRepaired
	 * @param id
     * @param state
	 * @return 
     */
    @Override
    public synchronized boolean backToWorkByBus(boolean carRepaired, int id, CustomerState state) {
		//((Customer) Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
        vehicleDriven[id]="--";
		repairShop.updateFromOutsideWorld(vehicleDriven);
        repairShop.updateFromOutsideWorld(id, state);
		if (!carRepaired) {
            waitingForCar.add(id);
            notifyAll();
            //System.err.println("Deadlock?"+id);
            
            //while (repairedCars.peek()!= id) {
            while (!repairedCars.contains(id)) {
                try {
                    wait();
                    System.out.println(id + " waiting to be called");
                    System.out.println(waitingForCar + " waitingforcar");
                    System.out.println(repairedCars + " repairedCars");
                } catch (InterruptedException e) {

                }
            }System.out.println(id + " called");
            System.out.println(repairedCars.toString());
            repairedCars.remove(new Integer(id));//repairedCars.remove(new Integer(id));
            System.out.println(id + "acordado");
            return true;
        }
        return carRepaired;
    }

    /**
     * Customer's method. After going back to work by car (with replacement
     * car), the customer waits for the manager to tell him that his car has
     * been repaired.
	 * @param carRepaired
	 * @param replacementCar
	 * @param id
	 * @return 
     */
    @Override
    public synchronized boolean backToWorkByCar(boolean carRepaired, int replacementCar, int id) {
        //((Customer) Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
        if(replacementCar==-1)
            if(id<10) vehicleDriven[id]="0"+Integer.toString(id);
			else vehicleDriven[id]=Integer.toString(id);
        else vehicleDriven[id]="R"+Integer.toString(replacementCar);
		
        repairShop.updateFromOutsideWorld(vehicleDriven);
		
        if (!carRepaired) {
            waitingForCar.add(id);
            notifyAll();
            while (!repairedCars.contains(id)) {
                try {
                    wait();
                    
                } catch (InterruptedException e) {

                }
            }
            return true;
        }
        return carRepaired;
    }

    /**
     * Customer's method. When the customer decides that he wants to repair his
     * car, he goes to the repair shop's park to park his car.
     */
    @Override
    public synchronized void goToRepairShop(int idCustomer, CustomerState state) {
		repairShop.updateFromOutsideWorld(idCustomer, state);
        //((Customer) Thread.currentThread()).setCustomerState(CustomerState.PARK);
        //System.out.println("Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - Going to repair shop.");
    }

    /**
     * Customer's method. After parking his car, the customer goes to the
     * reception to speak to the manager.
     */
    @Override
    public synchronized void goToReception(int idCustomer, CustomerState state) {
		repairShop.updateFromOutsideWorld(idCustomer, state);
        //((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
    }

    /**
     * Manager's method. After knowing that the car is ready by the mechanic,
     * the manager calls the customer to get his car back.
     *
     * @param id customer's id which car is ready to be picked up
     * @return a boolean representing if the customer is ready to pick up the
     * car
     */
    @Override
    public synchronized boolean phoneCustomer(int id) {
        System.err.println("vou ligar ao customer "+id);
        while (!waitingForCar.contains(id)) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        if (waitingForCar.contains(id)) {
            repairedCars.add(id);
            notifyAll();
            waitingForCar.remove(new Integer(id));
            System.err.println("ja liguei ao "+id);
            return true;
        } else {
            return false;
        }
    }
}
