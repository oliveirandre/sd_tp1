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
    private boolean availableCar = false;
    private int idCustomer = 0;
    private int idToCall = 0;

    public Manager(IManagerL lounge, IManagerRA repairArea, IManagerSS supplierSite, IManagerOW outsideWorld, IManagerP park, int nCustomers,  RepairShop repairShop) {
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
                    //System.out.println("Manager  - " + this.getManagerState());
                    /*noMoreTasks = lounge.enoughWork();
                    if(noMoreTasks)
                        break;*/
                    //System.out.println("-> " + leftCustomers + " | " + nCustomers + " <-");
                    if(leftCustomers == nCustomers) {
                        repairArea.enoughWork();
                        noMoreTasks = true;
                        break;
                    }
                    lounge.getNextTask();
                    lounge.appraiseSit();
                    /*if(action.equals("customer")) {
					 boolean isCarNeeded = lounge.talkWithCustomer();
					 }*/
                    break;

                case ATTENDING_CUSTOMER:
                    //System.out.println("Manager  - " + this.getManagerState());
                    //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    idCustomer = lounge.currentCustomer(); // nao ha distinçao de qual customer
                    //System.out.println(idCustomer);
                    availableCar = park.getReplacementCar();
                    //System.out.println(availableCar);
                    String action = lounge.talkWithCustomer(availableCar);
                    //System.out.println(action);
                    //System.out.println(action);
                    if (action.equals("car")) {
                        if (availableCar) {
                            park.reserveCar(idCustomer);
                            //System.out.println("CAR RESERVED FOR " + idCustomer);
                            lounge.handCarKey();
                            //System.out.println("HANDED.");
                            park.waitForCustomer(idCustomer);
                            //System.out.println("Client left.");
                        }
						repairArea.registerService(idCustomer);
                    } else if (action.equals("nocar")) {
                        repairArea.registerService(idCustomer);
                        //System.out.println("3");
                    } else {
                        //System.out.println("Pre receive payment");
                        lounge.receivePayment();
                        leftCustomers++;
                        //System.out.println("Manager - Customer payed.");
                        //System.out.println("4");
                        lounge.checkWhatToDo();
                    }
					
                    break;

                case GETTING_NEW_PARTS:
                    //System.out.println("Manager  - " + this.getManagerState());
                    partNeeded = lounge.getPieceToReStock();
					lounge.goReplenishStock();
                    break;

                case POSTING_JOB:
                    //System.out.println("Manager  - " + this.getManagerState());
                    lounge.checkWhatToDo();
                    break;

                case ALERTING_CUSTOMER:
                    //System.out.println("Manager  - " + this.getManagerState());
                    // wake up customer that has his car repaired
                    idToCall = lounge.getIdToCall();
                    //System.out.println("-----> Car "+ idToCall+ " is repaired!");
                    customerWaiting = lounge.alertCustomer(idToCall);
                    //System.out.println(customerWaiting);
                    if(!customerWaiting)
                        outsideWorld.phoneCustomer(idToCall);
                    /*customerWaiting = outsideWorld.phoneCustomer(idToCall);
                    //System.out.println(customerWaiting);
                    if(!customerWaiting)
                        lounge.alertCustomer(idToCall);*/
                    lounge.checkWhatToDo();
                    break;

                case REPLENISH_STOCK:
                    //System.out.println("Manager  - " + this.getManagerState());
                    
                    quant = supplierSite.goToSupplier(partNeeded);
					//System.out.println("PEÇA PRECISA TOTOROORROL:"+partNeeded.getTypePiece());
					int idToReFix = repairArea.storePart(partNeeded, quant);
					
					repairArea.registerService(idToReFix);
                    lounge.getNextTask();
                    break;
            }
        }
        //System.out.println("Manager dead.");
    }

    public void setManagerState(ManagerState state) {
		repairShop.reportStatus();
        if (this.state == state) {
            return;
        }
        this.state = state;
    }

    public ManagerState getManagerState() {
        return this.state;
    }
}
