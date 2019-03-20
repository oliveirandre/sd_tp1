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
    public boolean carRepaired = false;
    private boolean happyCustomer = false;
    private boolean carInRepairShop = false;
    public int replacementCar = 0;
    
    public Customer(ICustomerOW outsideWorld, ICustomerP park, ICustomerL lounge, int id) {
        this.outsideWorld = outsideWorld;
        this.park = park;
        this.lounge = lounge;
        this.id = id;
    }
    
    @Override
    public void run() {
        this.setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
        while(!this.happyCustomer) {
            switch(this.state) {
                case NORMAL_LIFE_WITH_CAR:
                    System.out.println("Customer " + this.id + " - " + this.getCustomerState());
                    if(!carInRepairShop) {
                            outsideWorld.decideOnRepair();
                            outsideWorld.goToRepairShop();
                    }
                    else
                            outsideWorld.goToRepairShop();
                    break;
                    
                case PARK:
                    System.out.println("Customer " + this.id + " - " + this.getCustomerState());
                    park.parkCar(this.id);
                    break;
                    
                case WAITING_FOR_REPLACE_CAR:
                    System.out.println("Customer " + this.id + " - " + this.getCustomerState());      
                    carInRepairShop = true;
                    replacementCar = park.findCar();
                    park.backToWorkByCar();
                    break;
                    
                case RECEPTION:
                    System.out.println("Customer " + this.id + " - " + this.getCustomerState());
                    lounge.queueIn(this.id);
                    if(!carRepaired) {
                        lounge.talkWithManager();
                        if(requiresCar)
                            lounge.collectKey();
                        else
                            outsideWorld.backToWorkByBus();
                    }
                    else {
                        lounge.payForTheService();
                        this.happyCustomer = true;
                        park.collectCar(this.id);
                        outsideWorld.backToWorkByCar();
                    }
                    break;
                    
                case NORMAL_LIFE_WITHOUT_CAR:
                    System.out.println("Customer " + this.id + " - " + this.getCustomerState());
                    outsideWorld.goToReception();
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
