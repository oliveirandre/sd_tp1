package entities;

/**
 *
 * @author Andre e Joao
 */

import shared.ILounge;
import shared.IOutsideWorld;
import shared.IPark;
import shared.IRepairArea;

public class Customer extends Thread {
    
    private CustomerState state;
    
    private final IOutsideWorld outsideWorld;
    private final IRepairArea repairArea;
    private final IPark park;
    private final ILounge lounge;
    
    private boolean happyCustomer = false;
    
    public Customer(IOutsideWorld outsideWorld, IPark park, ILounge lounge, IRepairArea repairArea) {
        this.outsideWorld = outsideWorld;
        this.park = park;
        this.lounge = lounge;
        this.repairArea = repairArea;
    }
    
    @Override
    public void run() {
        this.setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
        
        while(!this.happyCustomer) {
            switch(this.state) {
                
                case NORMAL_LIFE_WITH_CAR:
                    outsideWorld.decideOnRepair();
                    break;
                   
                case PARK:
                    park.queueIn();
                    
                    // after collecting repaired car
                    park.backToWorkByCar();
                    this.happyCustomer = true;
                    break;
                    
                case WAITING_FOR_REPLACE_CAR:
                    break;
                    
                case RECEPTION:
                    // when customer requires a repair
                    lounge.talkWithManager();
                    // when customer wants to get his repaired car
                    lounge.payForService();
                    lounge.collectCar();
                    break;
                    
                case NORMAL_LIFE_WITHOUT_CAR:
                    outsideWorld.queueIn();
                    break;
            }
        }
    }
    
    public void setCustomerState(CustomerState state) {
        if(state == this.state)
            return;
        this.state = state;
    }
    
    public CustomerState getCustomerState() {
        return this.state;
    }
            
}
