package entities;

/**
 *
 * @author Andre e Joao
 */

import shared.ILounge;
import shared.IOutsideWorld;
import shared.IPark;

public class Customer extends Thread {
    
    private CustomerState state;
    
    private final IOutsideWorld outsideWorld;
    private final IPark park;
    private final ILounge lounge;
    
    private boolean carRepaired = false;
    
    public Customer(IOutsideWorld outsideWorld, IPark park, ILounge lounge) {
        this.outsideWorld = outsideWorld;
        this.park = park;
        this.lounge = lounge;
    }
    
    @Override
    public void run() {
        while(!this.carRepaired) {
            switch(this.state) {
                case NORMAL_LIFE_WITH_CAR:
                    
                    break;
                case PARK:
                    break;
                case WAITING_FOR_REPLACE_CAR:
                    break;
                case RECEPTION:
                    // lounge.talkWithManager(); lounge.collectKey() ou lounge.backToWorkByBus()
                    // lounge.payForTheService(); lounge.collectCar();
					
					//this.carRepaired = true; dentro do lounge.payForTheService();
                    break;
                case NORMAL_LIFE_WITHOUT_CAR:
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
