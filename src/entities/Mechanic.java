package entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
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

	public Mechanic(IMechanicP park, IMechanicRA repairArea, IMechanicL lounge, int id) {
		this.park = park;
		this.repairArea = repairArea;
		this.lounge = lounge;
		this.id = id;
	}
	
	
	HashMap<Integer, Piece> piecesToBeRepaired;
	private boolean noMoreWork = false;
	boolean repairConcluded = false;
	Piece pieceManagerReStock;
	int idCarToFix = 0;

	@Override
	public void run() {
		this.setMechanicState(MechanicState.WAITING_FOR_WORK);

		while (!noMoreWork) {
			switch (this.state) {
				case WAITING_FOR_WORK:
					repairArea.readThePaper();
					//System.out.println("Mechanic " + this.id + " - Starting repair procedure");
					idCarToFix = repairArea.startRepairProcedure(); //acho que assim nao vai funcionar 
					//System.out.println("Going to repair car " + idCarToFix);
					
					//por causa dda situaÃ§ao em q o carro esta a espera de peça
					break;
				case FIXING_CAR:

					//System.out.println("Mechanic " + this.id + " - " + this.getMechanicState());
					park.getVehicle(idCarToFix);
					piecesToBeRepaired = repairArea.getPiecesToBeRepaired();
					if (!piecesToBeRepaired.containsKey(idCarToFix)) {
						repairArea.getRequiredPart(idCarToFix); //salta para CHECKING_STOCK
						break;
					}
					
					repairArea.fixIt(idCarToFix, piecesToBeRepaired.get(idCarToFix));
					//System.out.println("Mechanic " + this.id + " - " + idCarToFix + " Fixed");

					park.returnVehicle(idCarToFix);//estacionar o carro
					
                    //System.out.println("Mechanic " + this.id + " - " + idCarToFix + " Returning vehicle");

					repairArea.repairConcluded();
					repairConcluded = true;
					break;

				case ALERTING_MANAGER:
                    //System.out.println("Mechanic " + this.id + " - " + this.getMechanicState());
					
					if (!repairConcluded) {
						lounge.alertManager(piecesToBeRepaired.get(idCarToFix), idCarToFix);
					} else {
                        //System.out.println(idCarToFix + " FIXED");
						lounge.alertManager(null, idCarToFix);
					}
					repairConcluded = false;
					break;

				case CHECKING_STOCK:
					piecesToBeRepaired = repairArea.getPiecesToBeRepaired();
                    //System.out.println("Mechanic " + this.id + " - " + this.getMechanicState());
					if (!repairArea.partAvailable(piecesToBeRepaired.get(idCarToFix))) {
						repairArea.letManagerKnow(piecesToBeRepaired.get(idCarToFix), idCarToFix);
						//System.out.println("Mechanic " + this.id + " - There is no stock for car "+idCarToFix);
					} else {
						//System.out.println("Mechanic " + this.id + " - There is stock so let's proceed");
						repairArea.resumeRepairProcedure();
					}
					break;
			}
		}
	}

	public void setMechanicState(MechanicState state) {
		if (this.state == state) {
			return;
		}
		this.state = state;
	}

	public MechanicState getMechanicState() {
		return this.state;
	}
	
	public int getMechanicId(){
		return this.id;
	}
}