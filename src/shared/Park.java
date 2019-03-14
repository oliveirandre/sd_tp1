/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import entities.Customer;
import entities.CustomerState;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andre and joao
 */
public class Park implements IPark, ICustomerP, IMechanicP {
    
    private int parkingSlots = 50;
    private int replacementCars = 3;
    
    private List<Integer> carsParked = new ArrayList<Integer>();
    
    @Override
    public synchronized void parkCar(int id) {
        carsParked.add(id);
        parkingSlots--;
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
    }
    
    @Override
    public synchronized void backToWorkByCar() {
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void findCar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void queueIn() {
        
        ((Customer)Thread.currentThread()).setCustomerState(CustomerState.RECEPTION);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int getParkingSlots() {
        return this.parkingSlots;
    }
}
