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
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.GETTING_NEW_PARTS);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
