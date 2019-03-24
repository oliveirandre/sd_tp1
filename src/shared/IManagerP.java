/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

/**
 *
 * @author AndréOliveira
 */
public interface IManagerP {
    public boolean replacementCarAvailable();
    public void reserveCar(int id);
    public void waitForCustomer(int id);
}
