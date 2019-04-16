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
public interface ICustomerL {
	public void queueIn(int id, CustomerState state);
	public void talkWithManager(boolean carRepaired, boolean requiresCar, int idCustomer);
	public boolean collectKey(int id, CustomerState state);
	public void payForTheService();

	public int getCarReplacementId(int id);
}
