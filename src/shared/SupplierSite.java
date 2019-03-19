package shared;

import entities.Manager;
import entities.ManagerState;

/**
 *
 * @author andre and joao
 */
public class SupplierSite implements IManagerSS {
    
	@Override
    public synchronized void goToSupplier() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
    }
}
