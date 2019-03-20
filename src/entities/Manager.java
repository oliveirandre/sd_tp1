package entities;

import repository.Piece;
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
		int idCustomer = 0; //este idCustomer é o id que se manda ao mecânico
		while (!noMoreTasks) {
			switch (this.state) {

				case CHECKING_WHAT_TO_DO:
                                    System.out.println("Manager  - " + this.getManagerState());
                                    lounge.getNextTask();
                                    lounge.appraiseSit();
                                    /*if(action.equals("customer")) {
                                        boolean isCarNeeded = lounge.talkWithCustomer();
                                    }*/
                                    break;

				case ATTENDING_CUSTOMER:
                                    System.out.println("Manager  - " + this.getManagerState());
                                        idCustomer = lounge.currentCustomer();
					boolean isCarNeeded = lounge.talkWithCustomer();
					if (isCarNeeded) {
						lounge.handCarKey();
					}
                                        else {
                                            repairArea.registerService(idCustomer);
                                        }
					break;

				case GETTING_NEW_PARTS:
                                    System.out.println("Manager  - " + this.getManagerState());
					supplierSite.goToSupplier();
					break;

				case POSTING_JOB:
                                    System.out.println("Manager  - " + this.getManagerState());
					//repairArea.registerService(idCustomer); // wake up mechanic 
					//TEMOS DOIS REGISTERSERVICE FALAR DISTO COM CUNHA
					//URGENTEEEEEEEEEEE
					//UM NO LOUNGE E OUTRO NO REPAIRAREA
					lounge.getNextTask();
					break;

				case ALERTING_CUSTOMER:
                                    System.out.println("Manager  - " + this.getManagerState());
					// wake up customer that has his car repaired
					lounge.phoneCustomer(); // ou do outsideWorld
					lounge.getNextTask();
					break;

				case REPLENISH_STOCK:
                                    System.out.println("Manager  - " + this.getManagerState());
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
