package entities;

/**
 *
 * @author Andre e Joao
 */
public class Manager extends Thread {
    
    private ManagerState state;
    
    @Override
    public void run() {
       while(true) {
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
