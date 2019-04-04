package shared;

import entities.MechanicState;
import java.util.HashMap;
import repository.Piece;

/**
 *
 * @author andre e joao
 */
public interface IMechanicRA {

    public boolean readThePaper(int idMechanic, MechanicState state);

    public int startRepairProcedure();

    public void getRequiredPart(int id);

    public void letManagerKnow(Piece piece, int idCarToFix);

    public boolean partAvailable(Piece requiredPart, int idMechanic, MechanicState state);

    public int fixIt(int id, Piece p);

    public void getNextTask();

    public void resumeRepairProcedure();

    public void repairConcluded();

    public HashMap getPiecesToBeRepaired();

    public HashMap getPieces();
}
