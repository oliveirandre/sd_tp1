package entities;

import java.util.HashMap;
import repository.Piece;
import shared.IMechanicL;
import shared.IMechanicP;
import shared.IMechanicRA;

/**
 *
 * @author Andre e Joao
 */
public class Mechanic extends Thread {

    private MechanicState state;
    private final IMechanicP park;
    private final IMechanicRA repairArea;
    private final IMechanicL lounge;
    private final int id;

	/**
	 * Mechanic's constructor.
	 * @param park
	 * @param repairArea
	 * @param lounge
	 * @param id
	 */
	public Mechanic(IMechanicP park, IMechanicRA repairArea, IMechanicL lounge, int id) {
        this.park = park;
        this.repairArea = repairArea;
        this.lounge = lounge;
        this.id = id;
    }

    HashMap<Integer, Piece> piecesToBeRepaired;
    private boolean noMoreWork = false;
    boolean repairConcluded = false;
    private boolean enoughWork = false;
    Piece pieceManagerReStock;
    int idCarToFix = 0;

    @Override
    public void run() {
        this.setMechanicState(MechanicState.WAITING_FOR_WORK);

        while (!noMoreWork) {
            switch (this.state) {
                case WAITING_FOR_WORK:
                    enoughWork = repairArea.readThePaper(this.id, this.state);
                    if (enoughWork) {
                        noMoreWork = true;
                        break;
                    }

                    idCarToFix = repairArea.startRepairProcedure();
                    if(idCarToFix==-1) setMechanicState(MechanicState.WAITING_FOR_WORK);
                    else setMechanicState(MechanicState.FIXING_CAR);
                    break;
                case FIXING_CAR:

                    park.getVehicle(idCarToFix, this.id, this.state);

                    piecesToBeRepaired = repairArea.getPiecesToBeRepaired();
                    if (!piecesToBeRepaired.containsKey(idCarToFix)) {
                        repairArea.getRequiredPart(idCarToFix);
                        setMechanicState(MechanicState.CHECKING_STOCK);
                        break;
                    }

                    int fix = repairArea.fixIt(idCarToFix, piecesToBeRepaired.get(idCarToFix));
                    
                    if(fix==0){
                        //System.err.println("PEIDOLASPEIDOLASPEIDOLAS");
						setMechanicState(MechanicState.CHECKING_STOCK);
						break; //nao sei se este break é o melhor
					}
                    
                    park.returnVehicle(idCarToFix);

                    repairArea.repairConcluded(); //NAO FAZ NADA
                    setMechanicState(MechanicState.ALERTING_MANAGER);
                    
                    repairConcluded = true;
                    break;

                case ALERTING_MANAGER:
                    if (!repairConcluded) {
                        lounge.alertManager(piecesToBeRepaired.get(idCarToFix), idCarToFix, this.id, this.state);
                    } else {
                        lounge.alertManager(null, idCarToFix, this.id, this.state);
                    }
                    setMechanicState(MechanicState.WAITING_FOR_WORK);
                    repairConcluded = false;
                    break;

                case CHECKING_STOCK:
                    if (!repairArea.partAvailable(piecesToBeRepaired.get(idCarToFix), this.id, this.state)) {
                        repairArea.letManagerKnow(piecesToBeRepaired.get(idCarToFix), idCarToFix);
                        setMechanicState(MechanicState.ALERTING_MANAGER);
                        park.returnVehicle(idCarToFix);
                    } else {
                        repairArea.resumeRepairProcedure();//NAO FAZ NADA
                        setMechanicState(MechanicState.FIXING_CAR);
                    }
                    break;
            }
        }
    }
	
	/**
	 * Mechanic's method. Change state of mechanic and report status to log.
	 * 
	 * @param state state of mechanic
	 */
    private void setMechanicState(MechanicState state) {
        if (this.state == state) {
            return;
        }
        this.state = state;
    }

	/**
	 * Mechanic's method. Retrieves mechanic's state.
	 * @return mechanic's state
	 */
	public MechanicState getMechanicState() {
        return this.state;
    }

	/**
	 * Mechanic's method. Retrieves mechanic's id.
	 * @return mechanic's id
	 */
	public int getMechanicId() {
        return this.id;
    }
}
