package shared;

import entities.Customer;
import entities.CustomerState;
import entities.Manager;
import entities.ManagerState;
import entities.Mechanic;
import entities.MechanicState;
import java.util.Queue;

/**
 *
 * @author andre and joao
 */

public class Lounge implements ILounge, ICustomerL, IManagerL, IMechanicL {
    //antes dum wait há sempre while 
    //por exemplo o mecanico esta a espera enquanto nao houver peças ou carros para arranjar
    
    private Queue<Integer> customersQueue;
    
    @Override
    public synchronized void queueIn(int id) {
        customersQueue.add(id);
        while() {
            try {
                wait();
            } catch(Exception e) {
                
            }
        }
    }
    
    @Override
    public synchronized void talkWithManager() {
        queue.add(((Customer)Thread.currentThread()).getCustomerId());
        //((Customer)Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
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
    public synchronized void backToWorkByBus() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public synchronized void registerService() {
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.POSTING_JOB);
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
    
    @Override
    public synchronized void talkWithCustomer() {
        int customer = queue.poll();
        ((Manager)Thread.currentThread()).setManagerState(ManagerState.ATTENDING_CUSTOMER);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
