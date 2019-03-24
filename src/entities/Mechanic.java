package entities;

import java.util.HashMap;
import repository.Piece;
import repository.RepairShop;
import shared.IMechanicL;
import shared.IMechanicP;
import shared.IMechanicRA;

/**
 *
 * @author Andre e Joao
 */
public class Mechanic extends Thread {

	private MechanicState state;
	private RepairShop repairShop;
	private final IMechanicP park;
	private final IMechanicRA repairArea;
	private final IMechanicL lounge;
	private final int id;

	public Mechanic(IMechanicP park, IMechanicRA repairArea, IMechanicL lounge, int id, RepairShop repairShop) {
		this.park = park;
		this.repairArea = repairArea;
		this.lounge = lounge;
		this.id = id;
		this.repairShop = repairShop;
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
					enoughWork = repairArea.readThePaper();
					if (enoughWork) {
						noMoreWork = true;
						break;
					}

					idCarToFix = repairArea.startRepairProcedure();
					break;
				case FIXING_CAR:

					park.getVehicle(idCarToFix);

					piecesToBeRepaired = repairArea.getPiecesToBeRepaired();
					if (!piecesToBeRepaired.containsKey(idCarToFix)) {
						repairArea.getRequiredPart(idCarToFix);
						break;
					}

					repairArea.fixIt(idCarToFix, piecesToBeRepaired.get(idCarToFix));

					park.returnVehicle(idCarToFix);

					repairArea.repairConcluded();
					repairConcluded = true;
					break;

				case ALERTING_MANAGER:
					if (!repairConcluded) {
						lounge.alertManager(piecesToBeRepaired.get(idCarToFix), idCarToFix);
					} else {
						lounge.alertManager(null, idCarToFix);
					}
					repairConcluded = false;
					break;

				case CHECKING_STOCK:
					if (!repairArea.partAvailable(piecesToBeRepaired.get(idCarToFix))) {
						repairArea.letManagerKnow(piecesToBeRepaired.get(idCarToFix), idCarToFix);
						park.returnVehicle(idCarToFix);
					} else {
						repairArea.resumeRepairProcedure();
					}
					break;
			}
		}
	}

	public void setMechanicState(MechanicState state) {
		repairShop.reportStatus();
		if (this.state == state) {
			return;
		}
		this.state = state;
	}

	public MechanicState getMechanicState() {
		return this.state;
	}

	public int getMechanicId() {
		return this.id;
	}
}
