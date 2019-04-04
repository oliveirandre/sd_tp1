package shared;

import entities.MechanicState;

/**
 *
 * @author andre e joao
 */
public interface IMechanicP {
    public void getVehicle(int id, int idMechanic, MechanicState state);
    public void returnVehicle(int id);
}
