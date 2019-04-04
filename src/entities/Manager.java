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
	 */
	public Manager(IManagerL lounge, IManagerRA repairArea, IManagerSS supplierSite, IManagerOW outsideWorld, IManagerP park, int nCustomers) {
        this.lounge = lounge;
        this.repairArea = repairArea;
        this.supplierSite = supplierSite;
        this.outsideWorld = outsideWorld;
        this.park = park;
        this.nCustomers = nCustomers;
    }

    @Override
    public void run() {
        this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
        int nextTask = 0;
        while (!noMoreTasks) {
            switch (this.state) {
                case CHECKING_WHAT_TO_DO:
                    lounge.checkWhatToDo(this.state);
                    if (leftCustomers == nCustomers) {
                        repairArea.enoughWork();
                        noMoreTasks = true;
                        break;
                    }
                    lounge.getNextTask();
                    nextTask = lounge.appraiseSit();
                    switch (nextTask) {
                        case 1:
                            this.setManagerState(ManagerState.GETTING_NEW_PARTS);
                            break;
                        case 2:
                            this.setManagerState(ManagerState.ALERTING_CUSTOMER);
                            break;
                        case 3:
                            this.setManagerState(ManagerState.ATTENDING_CUSTOMER);
                            break;
                        default:
                            System.out.println("ERROR GETTING NEXT TASK");
                            break;
                    }
                    break;

                case ATTENDING_CUSTOMER:
                    idCustomer = lounge.currentCustomer(this.state);
                    availableReplacementCar = park.replacementCarAvailable();
                    String action = lounge.talkWithCustomer(availableReplacementCar);
                    if (action.equals("car")) {
                        if (availableReplacementCar) {
                            park.reserveCar(idCustomer);
                            lounge.handCarKey();
                            park.waitForCustomer(idCustomer);
                        }
                        this.setManagerState(ManagerState.POSTING_JOB);
                        //repairArea.registerService(idCustomer);
                    } else if (action.equals("nocar")) {
                        this.setManagerState(ManagerState.POSTING_JOB);
                        //repairArea.registerService(idCustomer);
                    } else {
                        lounge.receivePayment();
                        leftCustomers++;
                        this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
                        //lounge.checkWhatToDo();
                    }

                    break;

                case GETTING_NEW_PARTS:
                    partNeeded = lounge.getPieceToReStock(this.state);
                    //lounge.goReplenishStock();
                    this.setManagerState(ManagerState.REPLENISH_STOCK);
                    break;

                case POSTING_JOB:
                    repairArea.registerService(idCustomer, this.state);
                    this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
                    //lounge.checkWhatToDo();
                    break;

                case ALERTING_CUSTOMER:
                    idToCall = lounge.getIdToCall(this.state);
                    customerWaiting = lounge.alertCustomer(idToCall);
                    if (!customerWaiting) {
                        outsideWorld.phoneCustomer(idToCall);
                    }
                    this.setManagerState(ManagerState.CHECKING_WHAT_TO_DO);
                    //lounge.checkWhatToDo();
                    break;

                case REPLENISH_STOCK:
                    lounge.goReplenishStock(this.state);
                    quant = supplierSite.goToSupplier(partNeeded);
                    //int idToReFix = repairArea.storePart(partNeeded, quant);
                    idCustomer = repairArea.storePart(partNeeded, quant);
                    this.setManagerState(ManagerState.POSTING_JOB);
                    //repairArea.registerService(idToReFix);
                    //lounge.getNextTask();
                    break;
            }
        }
    }

	/**
	 * Manager's method. Change state of manager and report status to log.
	 * 
	 * @param state state of manager
	 */
	private void setManagerState(ManagerState state) {
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
