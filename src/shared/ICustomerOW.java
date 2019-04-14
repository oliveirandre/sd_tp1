/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import entities.CustomerState;

/**
 *
 * @author andre
 */
public interface ICustomerOW {
    public boolean decideOnRepair(int id, CustomerState state);
    public void goToRepairShop(int idCustomer, CustomerState state);
    public boolean backToWorkByCar(boolean carRepaired, int replacementCar, int id);
    public boolean backToWorkByBus(boolean carRepaired, int id, CustomerState state);
    public void goToReception(int idCustomer, CustomerState state);
}
