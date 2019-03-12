package entities;

import shared.IPark;
import shared.IRepairArea;
import shared.ILounge;

/**
 *
 * @author Andre e Joao
 */
public class Mechanic extends Thread {

    private MechanicState state;
    private final IPark park;
    private final IRepairArea repairArea;
    private final ILounge lounge;
    
    public Mechanic(IPark park, IRepairArea repairArea, ILounge lounge) {
        this.park = park;
	this.repairArea = repairArea;
        this.lounge = lounge;
    }
	
    @Override
    public void run() {
        this.setMechanicState(MechanicState.WAITING_FOR_WORK);
        
        while(true) {
            switch(this.state) {
                case WAITING_FOR_WORK:
                    repairArea.readThePaper();
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
