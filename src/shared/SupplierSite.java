package shared;

import entities.Manager;
import entities.ManagerState;

/**
 *
 * @author andre and joao
 */
public class SupplierSite implements ISupplierSite, IManagerSS {
    
	
	
    @Override
    public synchronized void storePart() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.REPLENISH_STOCK);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
