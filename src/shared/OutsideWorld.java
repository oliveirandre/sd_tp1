package shared;

import entities.Customer;
import entities.CustomerState;
import entities.Manager;
import entities.ManagerState;

/**
 *
 * @author andre and joao
 */
public class OutsideWorld implements IOutsideWorld {

	@Override
	public synchronized void decideOnRepair() {
		goToRepairShop();
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
