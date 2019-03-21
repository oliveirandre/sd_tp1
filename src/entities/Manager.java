package entities;

import repository.Piece;
import shared.IManagerL;
import shared.IManagerOW;
import shared.IManagerP;
import shared.IManagerRA;
import shared.IManagerSS;

/**
 *
 * @author Andre e Joao
 */
public class Manager extends Thread {

	private ManagerState state;

	private final IManagerL lounge;
	private final IManagerRA repairArea;
	private final IManagerSS supplierSite;
	private final IManagerOW outsideWorld;
	private final IManagerP park;

	private final boolean noMoreTasks = false;
	private boolean verifyReplacementCar = false;

	public Manager(IManagerL lounge, IManagerRA repairArea, IManagerSS supplierSite, IManagerOW outsideWorld, IManagerP park) {
		this.lounge = lounge;
		this.repairArea = repairArea;
		this.supplierSite = supplierSite;
		this.outsideWorld = outsideWorld;
		this.park = park;
	}

	@Override
	public void run() {
		this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
		int idCustomer = 0; //este idCustomer é o id que se manda ao mecânico

		int replacementCar = 0;
		int idToCall = 0;
		boolean availableCar = false;
		while (!noMoreTasks) {
			switch (this.state) {

				case CHECKING_WHAT_TO_DO:
					//System.out.println("Manager  - " + this.getManagerState());
					lounge.getNextTask();
					lounge.appraiseSit();
					/*if(action.equals("customer")) {
					 boolean isCarNeeded = lounge.talkWithCustomer();
					 }*/
					break;

				case ATTENDING_CUSTOMER:
					//System.out.println("Manager  - " + this.getManagerState());
					idCustomer = lounge.currentCustomer();

					if (park.getReplacementCarSize() == 1 && !verifyReplacementCar) {
						verifyReplacementCar = true;
						availableCar = true;
					} else if (verifyReplacementCar && park.getReplacementCarSize() == 1) {
						availableCar = false;
						verifyReplacementCar = true;
					}

					String action = lounge.talkWithCustomer(availableCar);
					//System.out.println(action);
					if (action.equals("car")) {
						if (availableCar) {
							lounge.handCarKey();
							repairArea.registerService(idCustomer);
							lounge.checkWhatToDo();
						} else {
							repairArea.registerService(idCustomer);
							lounge.checkWhatToDo();
						}
					} else if (action.equals("nocar")) {
						repairArea.registerService(idCustomer);
						lounge.checkWhatToDo();
					} else {
						//System.out.println("Receiving payment");
						lounge.receivePayment(action);
						//System.out.println("Manager - Customer payed.");
						lounge.checkWhatToDo();
					}

					break;

				case GETTING_NEW_PARTS:
					//System.out.println("Manager  - " + this.getManagerState());
					supplierSite.goToSupplier();
					break;

				case POSTING_JOB:
					//System.out.println("Manager  - " + this.getManagerState());
					lounge.checkWhatToDo();
					break;

				case ALERTING_CUSTOMER:
					//System.out.println("Manager  - " + this.getManagerState());
					// wake up customer that has his car repaired
					idToCall = lounge.getIdToCall();
					outsideWorld.phoneCustomer(idToCall); // ou do outsideWorld
					lounge.checkWhatToDo();
					break;

				case REPLENISH_STOCK:
					//System.out.println("Manager  - " + this.getManagerState());
					Piece partNeeded = lounge.getPieceToReStock();
					repairArea.storePart(partNeeded);
					lounge.getNextTask();
					break;
			}
		}
	}

	public void setManagerState(ManagerState state) {
		if (this.state == state) {
			return;
		}
		this.state = state;
	}

	public ManagerState getManagerState() {
		return this.state;
	}
}
