package shared;

import entities.Customer;
import entities.CustomerState;
import entities.Manager;
import entities.ManagerState;
import java.util.Random;

/**
 *
 * @author andre and joao
 */
public class OutsideWorld implements IOutsideWorld, ICustomerOW, IManagerOW {

	@Override
	public synchronized void decideOnRepair() {
            boolean decided = false;
            Random deciding = new Random();
            Random requiring = new Random();
            while(decided == false) {
                decided = deciding.nextBoolean();
                if(decided == true) {
                    ((Customer)Thread.currentThread()).requiresCar = requiring.nextBoolean();
                    goToRepairShop();
                }
            }
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void goToRepairShop() {
		((Customer) Thread.currentThread()).setCustomerState(CustomerState.PARK);
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void queueIn() {
		((Customer) Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public synchronized void phoneToCustomer() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void getNextTask() {
		((Manager) Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
