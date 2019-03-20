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

	/*
	 ** Customer's method. The customer starts his life span in the outside world
	 ** until he decides do repair his car. Furthermore, he also decides if he is
	 ** going to need a replacement car or not.
	 */
	@Override
	public synchronized void decideOnRepair() {
		boolean decided = false;
		Random deciding = new Random();
		//Random requiring = new Random();
		while (decided == false) {
			decided = deciding.nextBoolean();
			if (decided == true) {
				//((Customer) Thread.currentThread()).requiresCar = requiring.nextBoolean();
				((Customer) Thread.currentThread()).requiresCar = false;
				System.out.println("Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - I have decided to go to the repair shop and " + ((Customer) Thread.currentThread()).requiresCar + " a car.");
			}
		}
	}

	/*
	 ** Customer's method. After going back to work by bus, the customer waits
	 ** for the manager to tell him that his car has been repaired.
	 */
	@Override
	public synchronized void backToWorkByBus() {
		((Customer) Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
		System.out.println("Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - Back to Work by bus");
		while (!repairedCars.contains(((Customer) Thread.currentThread()).getCustomerId())) {
			try {
				wait();
				if (repairedCars.contains(((Customer) Thread.currentThread()).getCustomerId())) {
					((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
					repairedCars.remove(((Customer) Thread.currentThread()).getCustomerId());
					((Customer) Thread.currentThread()).carRepaired = true;
					return;
				}
			} catch (Exception e) {

			}
		}
	}

	@Override
	public synchronized void backToWorkByCar() {
		((Customer) Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
		if (((Customer) Thread.currentThread()).carRepaired) {
			return;
		} else {
			while (!repairedCars.contains(((Customer) Thread.currentThread()).getCustomerId())) {
				try {
					wait();
					if (repairedCars.contains(((Customer) Thread.currentThread()).getCustomerId())) {
						((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
						repairedCars.remove(((Customer) Thread.currentThread()).getCustomerId());
						((Customer) Thread.currentThread()).carRepaired = true;
						return;
					}
				} catch (Exception e) {

				}
			}
		}
	}

	/*
	 ** Customer's method. When the customer decides that he wants to repair his
	 ** car, he goes to the repair shop's park to park his car.
	 */
	@Override
	public synchronized void goToRepairShop() {
		((Customer) Thread.currentThread()).setCustomerState(CustomerState.PARK);
		System.out.println("Customer " + ((Customer) Thread.currentThread()).getCustomerId() + " - Going to repair shop.");
	}

	@Override
	public void goToReception() {
		((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
	}
}
