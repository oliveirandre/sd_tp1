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
	private final IMechanicP park;
	private final IMechanicRA repairArea;
	private final IMechanicL lounge;
	private final int id;
	private RepairShop repairShop;

	public Mechanic(IMechanicP park, IMechanicRA repairArea, IMechanicL lounge, int id, RepairShop repairShop) {
		this.park = park;
		this.repairArea = repairArea;
		this.lounge = lounge;
		this.id = id;
		this.repairShop = repairShop;
	}
	
	HashMap<Integer, Piece> pieceToBeRepaired;
        private boolean noMoreWork = false;
	boolean alreadyChecked = false;
	boolean repairConcluded = false;
	int idCarToFix;
	
	
	@Override
	public void run() {
		this.setMechanicState(MechanicState.WAITING_FOR_WORK);
		
		Piece pieceManagerReStock = null;
                int car = 0;
		while (!noMoreWork) {
			switch (this.state) {
				case WAITING_FOR_WORK:
					repairArea.readThePaper(); 
					car = repairArea.startRepairProcedure();
					break;
				case FIXING_CAR:
                                        park.getVehicle(car);
                                        park.returnVehicle(car);
                                        System.out.println("Mechanic - Car " + car + " repaired.");
                                        /*
					//idCarToFix = (int) lounge.getCarsToRepair().poll();//repairArea.getIdFromManager(); //manager tem que dizer qual o id aqui
					if (!alreadyChecked) {
						park.getVehicle(idCarToFix);

						pieceToBeRepaired = repairArea.getRequiredPart(idCarToFix); //salta para CHECKING_STOCK
						break;
					}

					repairArea.fixIt(idCarToFix, pieceToBeRepaired.get(idCarToFix));

					park.returnVehicle(idCarToFix);//estacionar o carro

					repairArea.repairConcluded(); //alertar manager
					repairConcluded = true;
					*/
					break;

				case ALERTING_MANAGER:
                                    lounge.alertManager(null, car);
                                    /*
					//alertar manager se foi repairConcluded ou se não há stock
					if(!repairConcluded)
						lounge.alertManager(pieceToBeRepaired.get(idCarToFix), idCarToFix);
					else
						lounge.alertManager(null, idCarToFix);
					break;

				case CHECKING_STOCK:

					//idCarToFix = 0; //manager tem que dizer qual o id aqui

					if (!repairArea.partAvailable(pieceToBeRepaired.get(idCarToFix))) {
						repairArea.letManagerKnow();
					} else {
						alreadyChecked = true;
						repairArea.resumeRepairProcedure();
					}
					break;*/
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
