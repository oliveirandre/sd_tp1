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
public interface ICustomerP {
	public void parkCar(int id, CustomerState state);
    public void collectCar(int id);
    public int findCar(int id, CustomerState state, int cust);
    public void returnReplacementCar(int replacementCar, int id, CustomerState state);
}
