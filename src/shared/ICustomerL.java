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
	public void talkWithManager();
	public void collectKey();
	public void payForTheService();
}
