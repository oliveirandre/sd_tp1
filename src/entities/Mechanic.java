package entities;

import repository.Piece;
import shared.ILounge;
import shared.IPark;
import shared.IRepairArea;

/**
 *
 * @author Andre e Joao
 */
public class Mechanic extends Thread {

	private MechanicState state;
	private final IPark park;
	private final IRepairArea repairArea;
	private final ILounge lounge;

	public Mechanic(IPark park, IRepairArea repairArea, ILounge lounge) {
		this.park = park;
		this.repairArea = repairArea;
		this.lounge = lounge;
	}

	@Override
	public void run() {
		this.setMechanicState(MechanicState.WAITING_FOR_WORK);

		while (true) {
			switch (this.state) {
				case WAITING_FOR_WORK:
					repairArea.readThePaper();
					// when awaken
					repairArea.startRepairProcedure();
					break;

				case FIXING_CAR:
					repairArea.getVehicle(); //repairArea ou park??????
					
					Piece requiredPart = repairArea.getRequiredPart();
					
					if(!repairArea.partAvailable(requiredPart)){
						repairArea.letManagerKnow();
						break;
					}
					repairArea.fixIt(requiredPart);
					repairArea.returnVehicle();
					repairArea.repairConcluded();
					break;

				case ALERTING_MANAGER:
					lounge.readThePaper();
					break;

				case CHECKING_STOCK:
					// if(partAvailable)
					repairArea.resumeRepairProcedure();
					// else
					repairArea.letManagerKnow();
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
