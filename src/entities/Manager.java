package entities;

import shared.ILounge;
import shared.IManagerL;
import shared.IOutsideWorld;
import shared.IPark;
import shared.IRepairArea;
import shared.ISupplierSite;

/**
 *
 * @author Andre e Joao
 */
public class Manager extends Thread {

	private ManagerState state;

	private final IManagerL lounge;
	private final IRepairArea repairArea;
	private final ISupplierSite supplierSite;
	private final IOutsideWorld outsideWorld;
	private final IPark park;

	private final boolean noMoreTasks = false;

	public Manager(IManagerL lounge, IRepairArea repairArea, ISupplierSite supplierSite, IOutsideWorld outsideWorld, IPark park) {
		this.lounge = lounge;
		this.repairArea = repairArea;
		this.supplierSite = supplierSite;
		this.outsideWorld = outsideWorld;
		this.park = park;
	}

	@Override
	public void run() {
		this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);

		while (!noMoreTasks) {
			switch (this.state) {

				case ATTENDING_CUSTOMER:
					lounge.receivePayment();
					lounge.handCarKey();
					lounge.registerService();
					break;

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

				case GETTING_NEW_PARTS:
					supplierSite.storePart();
					break;

				case POSTING_JOB:
					// wake up mechanic
					repairArea.getNextTask();
					break;

				case ALERTING_CUSTOMER:
					// wake up customer that has his car repaired
					outsideWorld.getNextTask();
					break;

				case REPLENISH_STOCK:
					repairArea.storePart();
					repairArea.getNextTask();
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
