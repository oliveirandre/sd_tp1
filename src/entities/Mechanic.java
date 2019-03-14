package entities;

import repository.Piece;
import shared.ILounge;
import shared.IMechanicL;
import shared.IMechanicP;
import shared.IMechanicRA;
import shared.IPark;
import shared.IRepairArea;

/**
 *
 * @author Andre e Joao
 */
public class Mechanic extends Thread {

    private MechanicState state;
    private final IMechanicP park;
    private final IMechanicRA repairArea;
    private final IMechanicL lounge;

    public Mechanic(IMechanicP park, IMechanicRA repairArea, IMechanicL lounge) {
        this.park = park;
        this.repairArea = repairArea;
        this.lounge = lounge;
    }

    @Override
    public void run() {
        this.setMechanicState(MechanicState.WAITING_FOR_WORK);

        while (true) {
            switch (this.state) {
                case WAITING_FOR_WORK:
                    repairArea.readThePaper();
                    repairArea.startRepairProcedure(); // when awaken
                    break;
                case FIXING_CAR:
                    int idCarToFix = 0; //manager tem que dizer qual o id aqui
                    park.getVehicle(idCarToFix);
                    Piece requiredPart = repairArea.getRequiredPart();

                    if (!repairArea.partAvailable(requiredPart)) {
                        repairArea.letManagerKnow();
                        break;
                    }
                    repairArea.fixIt(requiredPart);
                    park.returnVehicle(idCarToFix);//estacionar o carro
                    
                    repairArea.repairConcluded(); //alertar manager
                    
                    break;

                case ALERTING_MANAGER:
                    //lounge.readThePaper();
                    break;

                case CHECKING_STOCK:
                    // if(partAvailable)
                    //repairArea.resumeRepairProcedure();
                    // else
                    //repairArea.letManagerKnow();
                    break;
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
