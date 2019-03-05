package entities;

/**
 *
 * @author Andre e Joao
 */
public class Customer extends Thread {
    
    private CustomerState state;
    
    @Override
    public void run() {
        while(true) {
            switch(this.state) {
                case NORMAL_LIFE_WITH_CAR:
                    break;
                case PARK:
                    break;
                case WAITING_FOR_REPLACE_CAR:
                    break;
                case RECEPTION:
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
