package entities;

import shared.IManagerL;
import shared.IManagerOW;
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

	private final boolean noMoreTasks = false;

	public Manager(IManagerL lounge, IManagerRA repairArea, IManagerSS supplierSite, IManagerOW outsideWorld) {
		this.lounge = lounge;
		this.repairArea = repairArea;
		this.supplierSite = supplierSite;
		this.outsideWorld = outsideWorld;
	}

	@Override
	public void run() {
		this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);

		while (!noMoreTasks) {
			switch (this.state) {
				
				case CHECKING_WHAT_TO_DO:
					lounge.getNextTask();
					boolean isCarNeeded = lounge.talkWithCustomer();
					if (isCarNeeded) {
						lounge.handCarKey();
					}
					// after posting job
					lounge.phoneCustomer();
					// after alerting customer
					lounge.goToSupplier();
					// if there are no more tasks
					lounge.appraiseSit();
					break;
					
				case ATTENDING_CUSTOMER:
					lounge.receivePayment();
					lounge.handCarKey();
					lounge.registerService();
					break;

				case GETTING_NEW_PARTS:
					//supplierSite.storePart();
					break;

				case POSTING_JOB:
					// wake up mechanic
					lounge.getNextTask();
					break;

				case ALERTING_CUSTOMER:
					// wake up customer that has his car repaired
					lounge.getNextTask();
					break;

				case REPLENISH_STOCK:
					//repairArea.storePart();
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
