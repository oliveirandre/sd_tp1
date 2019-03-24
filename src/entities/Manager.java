package entities;

import repository.Piece;
import repository.RepairShop;
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
    private RepairShop repairShop;

    private final IManagerL lounge;
    private final IManagerRA repairArea;
    private final IManagerSS supplierSite;
    private final IManagerOW outsideWorld;
    private final IManagerP park;
    private boolean customerWaiting = false;
    private int quant;
    Piece partNeeded;
    private boolean noMoreTasks = false;
    private int nCustomers;
    private int leftCustomers = 0;
    private boolean availableReplacementCar = false;
    private int idCustomer = 0;
    private int idToCall = 0;

	/**
	 * Manager's constructor.
	 * @param lounge
	 * @param repairArea
	 * @param supplierSite
	 * @param outsideWorld
	 * @param park
	 * @param nCustomers
	 * @param repairShop
	 */
	public Manager(IManagerL lounge, IManagerRA repairArea, IManagerSS supplierSite, IManagerOW outsideWorld, IManagerP park, int nCustomers, RepairShop repairShop) {
        this.lounge = lounge;
        this.repairArea = repairArea;
        this.supplierSite = supplierSite;
        this.outsideWorld = outsideWorld;
        this.park = park;
        this.nCustomers = nCustomers;
        this.repairShop = repairShop;
    }

    @Override
    public void run() {
        this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
        while (!noMoreTasks) {
            switch (this.state) {
                case CHECKING_WHAT_TO_DO:
                    if (leftCustomers == nCustomers) {
                        repairArea.enoughWork();
                        noMoreTasks = true;
                        break;
                    }
                    lounge.getNextTask();
                    lounge.appraiseSit();
                    break;

                case ATTENDING_CUSTOMER:
                    idCustomer = lounge.currentCustomer();
                    availableReplacementCar = park.replacementCarAvailable();
                    String action = lounge.talkWithCustomer(availableReplacementCar);
                    if (action.equals("car")) {
                        if (availableReplacementCar) {
                            park.reserveCar(idCustomer);
                            lounge.handCarKey();
                            park.waitForCustomer(idCustomer);
                        }
                        repairArea.registerService(idCustomer);
                    } else if (action.equals("nocar")) {
                        repairArea.registerService(idCustomer);
                    } else {
                        lounge.receivePayment();
                        leftCustomers++;
                        lounge.checkWhatToDo();
                    }

                    break;

                case GETTING_NEW_PARTS:
                    partNeeded = lounge.getPieceToReStock();
                    lounge.goReplenishStock();
                    break;

                case POSTING_JOB:
                    lounge.checkWhatToDo();
                    break;

                case ALERTING_CUSTOMER:
                    idToCall = lounge.getIdToCall();
                    customerWaiting = lounge.alertCustomer(idToCall);
                    if (!customerWaiting) {
                        outsideWorld.phoneCustomer(idToCall);
                    }
                    lounge.checkWhatToDo();
                    break;

                case REPLENISH_STOCK:
                    quant = supplierSite.goToSupplier(partNeeded);
                    int idToReFix = repairArea.storePart(partNeeded, quant);
                    repairArea.registerService(idToReFix);
                    lounge.getNextTask();
                    break;
            }
        }
    }

	/**
	 * Manager's method. Change state of manager and report status to log.
	 * 
	 * @param state state of manager
	 */
	public void setManagerState(ManagerState state) {
        repairShop.reportStatus();
        if (this.state == state) {
            return;
        }
        this.state = state;
    }

	/**
	 * Manager's method. Retrieves manager's state.
	 * @return manager's state
	 */
	public ManagerState getManagerState() {
        return this.state;
    }
}
