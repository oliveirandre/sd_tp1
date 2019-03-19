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
					lounge.appraiseSit(); //while customersQueue is empty, waits
                    
					System.out.println("?=?");
					
					lounge.getNextTask(); 
					// after posting job
					//lounge.phoneCustomer();
					// after alerting customer
					
					
					
                    //System.out.println("JDAIWJDAW");
	
					break;

				case ATTENDING_CUSTOMER:
					boolean isCarNeeded = lounge.talkWithCustomer();
					System.out.println(isCarNeeded);
					if (isCarNeeded) {
						lounge.handCarKey();
					}
					lounge.receivePayment();
					lounge.handCarKey();
					
					break;

				case GETTING_NEW_PARTS:
					supplierSite.goToSupplier();
					break;

				case POSTING_JOB:
					repairArea.registerService(idCustomer); // wake up mechanic 
					//TEMOS DOIS REGISTERSERVICE FALAR DISTO COM CUNHA
					//URGENTEEEEEEEEEEE
					//UM NO LOUNGE E OUTRO NO REPAIRAREA
					lounge.getNextTask();
					break;

				case ALERTING_CUSTOMER:
					// wake up customer that has his car repaired
					lounge.phoneCustomer(); // ou do outsideWorld
					lounge.getNextTask();
					break;

				case REPLENISH_STOCK:
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
