package entities;

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
    
    public Mechanic(IPark park, IRepairArea repairArea) {
        this.park = park;
		this.repairArea = repairArea;
    }
	
    @Override
    public void run() {
        while(true) {
            switch(this.state) {
                case WAITING_FOR_WORK:
                    break;
                case FIXING_CAR:
					repairArea.getVehicle(); //repairArea ou park??????
					repairArea.getRequiredPart();
					if(repairArea.partAvailable())
						repairArea.resumeRepairProcedure();
					else
						repairArea.letManagerKnow();
                    break;
                case ALERTING_MANAGER:
					repairArea.readThePaper();
                    break;
                case CHECKING_STOCK:
                    break;
            }
        }    
    }
    
    public void setMechanicState(MechanicState state) {
        if(this.state == state)
            return;
        this.state = state;
    }
    
    public MechanicState getMechanicState() {
        return this.state;
    }
    
}
