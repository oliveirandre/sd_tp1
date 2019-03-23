package shared;

import entities.Customer;
import entities.CustomerState;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author andre and joao
 */
public class OutsideWorld implements ICustomerOW, IManagerOW {

    private final List<Integer> repairedCars = new ArrayList<>();
    private final List<Integer> waitingForCar = new ArrayList<>();

    /*
	 ** Customer's method. The customer starts his life span in the outside world
	 ** until he decides do repair his car. Furthermore, he also decides if he is
	 ** going to need a replacement car or not.
     */
    @Override
    public synchronized void decideOnRepair() {
        Random requires = new Random();
        Random n = new Random();
        int randomNum = 0;
        while(randomNum != 1) {
            randomNum = n.nextInt((100000 - 1) + 1) + 1;
        }
        ((Customer) Thread.currentThread()).requiresCar = false;
	}

    /*
	 ** Customer's method. After going back to work by bus, the customer waits
	 ** for the manager to tell him that his car has been repaired.
     */
    @Override
    public synchronized void backToWorkByBus() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
        //waitingForCar.add(((Customer) Thread.currentThread()).getCustomerId());
        //System.out.println(waitingForCar.toString());
        //System.out.println("Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - Back to Work by bus");
        if (!((Customer) Thread.currentThread()).carRepaired) {
            //System.out.println("OUTSIDE WORLD WITH CAR");
            waitingForCar.add(((Customer) Thread.currentThread()).getCustomerId());
            notifyAll();
            while (!repairedCars.contains(((Customer) Thread.currentThread()).getCustomerId())) {
                try {
                    wait();
                } catch (Exception e) {

                }
            }
            repairedCars.remove(new Integer(((Customer) Thread.currentThread()).getCustomerId()));
            ((Customer) Thread.currentThread()).carRepaired = true;
        }
    }

    @Override
    public synchronized void backToWorkByCar() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
        //System.out.println(waitingForCar.toString());
        System.out.println(((Customer) Thread.currentThread()).getCustomerId() + " - OUTSIDE WORLD WITH CAR REPAIRED");
        if (!((Customer) Thread.currentThread()).carRepaired) {
            waitingForCar.add(((Customer) Thread.currentThread()).getCustomerId());
            notifyAll();
            while (!repairedCars.contains(((Customer) Thread.currentThread()).getCustomerId())) {
                try {
                    wait();
                } catch (Exception e) {

                }
            }
            ((Customer) Thread.currentThread()).carRepaired = true;
            //waitingForCar.remove(((Customer) Thread.currentThread()).getCustomerId());
        }
    }

    /*
	 ** Customer's method. When the customer decides that he wants to repair his
	 ** car, he goes to the repair shop's park to park his car.
     */
    @Override
    public synchronized void goToRepairShop() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.PARK);
        //System.out.println("Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - Going to repair shop.");
    }

    @Override
    public synchronized void goToReception() {
        ((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
    }

    @Override
    public synchronized boolean phoneCustomer(int id) {
        while(!waitingForCar.contains(id)) {
            try {
                wait();
            } catch(Exception e) {
                
            }
        }
        
        //System.out.println(id + " - " + waitingForCar.toString());
        //System.out.println(waitingForCar.contains(id));
        if(waitingForCar.contains(id)) {
            repairedCars.add(id);
            System.out.println("CARS REPAIRED " + repairedCars.toString());
            notifyAll();
            waitingForCar.remove(new Integer(id));
            return true;
        }
        else
            return false;
    }
}
