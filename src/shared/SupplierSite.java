package shared;

import entities.Manager;
import entities.ManagerState;

/**
 *
 * @author andre and joao
 */
public class SupplierSite implements IManagerSS {
    
	int randomNum = 1 + (int)(Math.random() * ((5 - 1) + 1)); //between 1 and 6
	
	@Override
    public synchronized int goToSupplier() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
		return randomNum;
    }
}
