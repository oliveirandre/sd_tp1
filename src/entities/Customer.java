package entities;

/**
 *
 * @author Andre e Joao
 */

import shared.ICustomerL;
import shared.ICustomerOW;
import shared.ICustomerP;

public class Customer extends Thread {
    
    private CustomerState state;
    private final int id;
    
    private final ICustomerOW outsideWorld;
    private final ICustomerP park;
    private final ICustomerL lounge;
    
    // generate automatically if customer requires a replacement car
    public boolean requiresCar = false;
    private boolean happyCustomer = false;
    
    public Customer(ICustomerOW outsideWorld, ICustomerP park, ICustomerL lounge, int id) {
        this.outsideWorld = outsideWorld;
        this.park = park;
        this.lounge = lounge;
        this.id = id;
    }
    
    @Override
    public void run() {
        this.setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
        System.out.println(this.getCustomerState());
        while(!this.happyCustomer) {
            switch(this.state) {
                
                case NORMAL_LIFE_WITH_CAR:
                    outsideWorld.decideOnRepair();
                    
                    
                    // if customer has replace car
                    outsideWorld.goToRepairShop();
                    // if car is repaired
                    this.happyCustomer = true;
                    break;
                   
                case PARK:
                    // park car in need of a repair
                    park.parkCar(this.id);
                    park.queueIn();
                    // if customer required a replace car
                    park.backToWorkByCar();
                    // after collecting repaired car
                    park.backToWorkByCar();
                    break;
                    
                case WAITING_FOR_REPLACE_CAR:
                    lounge.findCar();
                    break;
                    
                case RECEPTION:
                    lounge.queueIn(id);
                    // when customer requires a repair
                    lounge.talkWithManager();
                    // goes back to work by bus
                    lounge.backToWorkByBus();
                    // or asks for a replacement car
                    lounge.collectKey();
                    // when customer wants to get his repaired car
                    lounge.payForService();
                    lounge.collectCar();
                    break;
                    
                case NORMAL_LIFE_WITHOUT_CAR:
                    //outsideWorld.queueIn();
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
    
    public int getCustomerId() {
        return this.id;
    }
            
}
