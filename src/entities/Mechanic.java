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

	public Mechanic(IMechanicP park, IMechanicRA repairArea, IMechanicL lounge) {
		this.park = park;
		this.repairArea = repairArea;
		this.lounge = lounge;
	}
	HashMap<Integer, Piece> pieceToBeRepaired;
	boolean alreadyChecked = false;

	@Override
	public void run() {
		this.setMechanicState(MechanicState.WAITING_FOR_WORK);

		while (true) {
			switch (this.state) {
				case WAITING_FOR_WORK:
					repairArea.readThePaper(); //falta fazer wait
					repairArea.startRepairProcedure(); // when awaken
					break;
				case FIXING_CAR:
					int idCarToFix = 0; //manager tem que dizer qual o id aqui
					if (!alreadyChecked) {
						park.getVehicle(idCarToFix);

						pieceToBeRepaired = repairArea.getRequiredPart(idCarToFix); //salta para CHECKING_STOCK
						break;
					}

					repairArea.fixIt(idCarToFix, pieceToBeRepaired.get(idCarToFix));

					park.returnVehicle(idCarToFix);//estacionar o carro

					repairArea.repairConcluded(); //alertar manager

					break;

				case ALERTING_MANAGER:
					//alertar manager se foi repairConcluded ou se não há stock
					lounge.readThePaper();
					break;

				case CHECKING_STOCK:

					idCarToFix = 0; //manager tem que dizer qual o id aqui

					if (!repairArea.partAvailable(pieceToBeRepaired.get(idCarToFix))) {
						repairArea.letManagerKnow();
					} else {
						alreadyChecked = repairArea.resumeRepairProcedure();
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
