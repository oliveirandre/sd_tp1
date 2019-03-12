package shared;

import entities.Customer;
import entities.CustomerState;

/**
 *
 * @author andre and joao
 */
public class OutsideWorld implements IOutsideWorld{

	@Override
	public synchronized void decideOnRepair() {
            goToRepairShop();
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public synchronized void goToRepairShop() {
            ((Customer)Thread.currentThread()).setCustomerState(CustomerState.PARK);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void queueIn() {
            ((Customer)Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
        
        public synchronized void phoneToCustomer() {
        }
    
}
