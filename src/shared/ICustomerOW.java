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
    public void decideOnRepair(int id, CustomerState state);
    public void goToRepairShop(int idCustomer, CustomerState state);
    public void backToWorkByBus();
    public void backToWorkByCar();
    public void goToReception(int idCustomer, CustomerState state);
}
