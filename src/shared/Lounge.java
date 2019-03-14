package shared;

import entities.Customer;
import entities.CustomerState;
import entities.Manager;
import entities.ManagerState;
import entities.Mechanic;
import entities.MechanicState;
import java.util.HashMap;
import java.util.Queue;

/**
 *
 * @author andre and joao
 */

public class Lounge implements ICustomerL, IManagerL, IMechanicL {
    
    private Queue<Integer> customersQueue;
    private Queue<Integer> carsToRepair;
    private int nextCustomer;
    private HashMap<Integer, Boolean> requiresCar = new HashMap<Integer, Boolean>();
    
    /*
    ** Customer's method. After parking the car in need of a repair, the custo-
    ** mer now has to wait in a queue to be attended by the manager.
    */       
    @Override
    public synchronized void queueIn(int id) {
        customersQueue.add(id);
        while(!(nextCustomer == id)) {
            try {
                wait();
                if(nextCustomer == id) {
                    return;
                }
            } catch(Exception e) {
                
            }
        }
    }
    
    /*
    ** Customer's method. When the customer is talking to the manager he says if
    ** he requires a replacement car or not.
    */
    @Override
    public synchronized void talkWithManager() {
        requiresCar.put(nextCustomer, ((Customer)Thread.currentThread()).requiresCar);
        notifyAll();
        if(((Customer)Thread.currentThread()).requiresCar) {
            while(true) {
                try {
                    wait();
                } catch(Exception e) {
                    
                }
            }
        }
        else {
            return;
        }
    }    
    
    /*
    ** Customer's method. When the customer doesn't require a replacement car,
    ** he goes back to work by bus.
    */
    @Override
    public synchronized void backToWorkByBus() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
    }
    
    @Override
    public synchronized boolean talkWithCustomer() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        nextCustomer = customersQueue.poll();
        notifyAll();
        while(!(requiresCar.containsKey(nextCustomer))) {
            try {
                wait();
                if(requiresCar.containsKey(nextCustomer)) {
                    if(requiresCar.get(nextCustomer) == true)
                        return true;            
                    else
                        return false;
                }
            } catch(Exception e) {
                
            }
        }
        return false;
    }

    @Override
    public synchronized void handCarKey() {
        
    }
    
    /*
    ** Manager's method. After receiving a request from a customer, the manager 
    ** registers it for further use by the mechanics.
    */
    @Override
    public synchronized void registerService() {
        carsToRepair.add(nextCustomer);
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.POSTING_JOB);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
    
    @Override
    public synchronized void collectKey() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);
        while(true) {
            try {
                wait();
            } catch(Exception e) {
                
            }
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /*
    @Override
    public synchronized void collectCar() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.PARK);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void payForService() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    @Override
    public synchronized void getNextTask() {
        // manager gets the next task: can be talkToCustomer, phoneCustomer, goToSupplier
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void goToSupplier() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.GETTING_NEW_PARTS);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void receivePayment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void handCarKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void findCar() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.PARK);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void appraiseSit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void phoneCustomer() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.ALERTING_CUSTOMER);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void readThePaper() {
        ((Mechanic)Thread.currentThread()).setMechanicState(MechanicState.WAITING_FOR_WORK);
    }
    */
}
