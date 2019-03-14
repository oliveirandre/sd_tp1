package shared;

import entities.Customer;
import entities.CustomerState;
import entities.Manager;
import entities.ManagerState;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author andre and joao
 */
public class OutsideWorld implements IOutsideWorld, ICustomerOW, IManagerOW {

    private Queue<Integer> repairedCars;

    @Override
    public synchronized void decideOnRepair() {
        Random random = new Random();
        ((Customer)Thread.currentThread()).requiresCar = random.nextBoolean();
        goToRepairShop();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void goToRepairShop() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.PARK);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void queueIn() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public synchronized void phoneToCustomer() {
        notifyAll();
    }

    @Override
    public synchronized void getNextTask() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
