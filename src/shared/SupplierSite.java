/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
