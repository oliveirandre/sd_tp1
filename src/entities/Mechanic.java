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

	public Mechanic(IMechanicP park, IMechanicRA repairArea, IMechanicL lounge, int id) {
		this.park = park;
		this.repairArea = repairArea;
		this.lounge = lounge;
		this.id = id;
	}

	HashMap<Integer, Piece> piecesToBeRepaired;
	private boolean noMoreWork = false;
	boolean alreadyChecked = false;
	boolean repairConcluded = false;
	Piece pieceManagerReStock;
	int idCarToFix = 0;

	@Override
	public void run() {
		this.setMechanicState(MechanicState.WAITING_FOR_WORK);
		
		
		while (!noMoreWork) {
			switch (this.state) {
				case WAITING_FOR_WORK:
					System.out.println("Mechanic " + this.id + " - Waiting for work...");
					repairArea.readThePaper();
					idCarToFix = repairArea.startRepairProcedure(); //acho que assim nao vai funcionar por causa dda situaÃ§ao em q o carro esta a espera de peÃ§a
					break;
				case FIXING_CAR:
					System.out.println("Mechanic " + this.id + " - " + this.getMechanicState());
					park.getVehicle(idCarToFix);
					
					if (!alreadyChecked) {
						piecesToBeRepaired = repairArea.getRequiredPart(idCarToFix); //salta para CHECKING_STOCK
						break;
					}
					

					repairArea.fixIt(idCarToFix, piecesToBeRepaired.get(idCarToFix));
					System.out.println("Mechanic " + this.id + " - Fixed");
					
					park.returnVehicle(idCarToFix);//estacionar o carro
					System.out.println("Mechanic " + this.id + " - Returning vehicle");
					
					repairArea.repairConcluded(); //alertar manager
					repairConcluded = true;
					alreadyChecked = false;
					break;

				case ALERTING_MANAGER:
					if (!repairConcluded) {
						lounge.alertManager(piecesToBeRepaired.get(idCarToFix), idCarToFix);
					} else {
						lounge.alertManager(null, idCarToFix);
					}
					break;

				case CHECKING_STOCK:
					if (!repairArea.partAvailable(piecesToBeRepaired.get(idCarToFix))) {
						repairArea.letManagerKnow();
						System.out.println("Mechanic " + this.id + " - There is no stock");
					} else {
						alreadyChecked = true;
						System.out.println("Mechanic " + this.id + " - There is stock so let's proceed");
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

}
