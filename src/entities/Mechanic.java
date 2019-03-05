package entities;

/**
 *
 * @author Andre e Joao
 */
public class Mechanic extends Thread {

    private MechanicState state;
    
    @Override
    public void run() {
        while(true) {
            switch(this.state) {
                case WAITING_FOR_WORK:
                    break;
                case FIXING_CAR:
                    break;
                case ALERTING_MANAGER:
                    break;
                case CHECKING_STOCK:
                    break;
            }
        }    
    }
    
    public void setMechanicState(MechanicState state) {
        if(this.state == state)
            return;
        this.state = state;
    }
    
    public MechanicState getMechanicState() {
        return this.state;
    }
    
}
