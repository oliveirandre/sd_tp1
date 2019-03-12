package entities;

import shared.ILounge;
import shared.IRepairArea;
import shared.ISupplierSite;
import shared.IOutsideWorld;
import shared.IPark;

/**
 *
 * @author Andre e Joao
 */
public class Manager extends Thread {
    
    private ManagerState state;
    
    private final ILounge lounge;
    private final IRepairArea repairArea;
    private final ISupplierSite supplierSite;
    private final IOutsideWorld outsideWorld;
    private final IPark park;
    
    private final boolean noMoreTasks = false;
    
    public Manager(ILounge lounge, IRepairArea repairArea, ISupplierSite supplierSite, IOutsideWorld outsideWorld, IPark park) {
        this.lounge = lounge;
	this.repairArea = repairArea;
        this.supplierSite = supplierSite;
        this.outsideWorld = outsideWorld;
        this.park = park;
    }
	
    @Override
    public void run() {
        this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
        
        while(!noMoreTasks) {
            switch(this.state) {
                
                case ATTENDING_CUSTOMER:
                    break;
                    
                case CHECKING_WHAT_TO_DO:
                    break;
                    
                case GETTING_NEW_PARTS:
                    break;
                    
                case POSTING_JOB:
                    break;
                    
                case ALERTING_CUSTOMER:
                    break;
                    
                case REPLENISH_STOCK:
                    break;
            }
        }             
    }
    
    public void setManagerState(ManagerState state) {
        if(this.state == state)
            return;
        this.state = state;
    }
    
    public ManagerState getManagerState() {
        return this.state;
    }
}
