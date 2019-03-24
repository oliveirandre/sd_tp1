package entities;

/**
 *
 * @author Andre e Joao
 */
import repository.RepairShop;
import shared.ICustomerL;
import shared.ICustomerOW;
import shared.ICustomerP;

public class Customer extends Thread {

    private CustomerState state;
	private RepairShop repairShop;

    private final int id;

    private final ICustomerOW outsideWorld;
    private final ICustomerP park;
    private final ICustomerL lounge;

    // generate automatically if customer requires a replacement car
    public boolean requiresCar = false;
    public boolean carRepaired = false;
    private boolean happyCustomer = false;
    private boolean haveReplacementCar = false;
    public int replacementCar;
	private boolean haveCar = true;

    public Customer(ICustomerOW outsideWorld, ICustomerP park, ICustomerL lounge, int id, RepairShop repairShop) {
        this.outsideWorld = outsideWorld;
        this.park = park;
        this.lounge = lounge;
        this.id = id;
		this.repairShop = repairShop;
    }

    @Override
    public void run() {
        this.setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
        while (!this.happyCustomer) {
            switch (this.state) {
                case NORMAL_LIFE_WITH_CAR:
                    haveCar=true;
                    if (!haveReplacementCar) {
                        outsideWorld.decideOnRepair();
                    }
					outsideWorld.goToRepairShop();
                    break;

                case PARK:
                    if (!haveReplacementCar) {
                        park.parkCar(this.id);
                    } else {
                        park.returnReplacementCar(replacementCar);
						haveReplacementCar = false;
                    }
                    break;

                case WAITING_FOR_REPLACE_CAR:
                    replacementCar = park.findCar();
                    haveReplacementCar = true;
                    outsideWorld.backToWorkByCar();
                    break;

                case RECEPTION:
                    lounge.queueIn(this.id);
                    if (!carRepaired) {
                        lounge.talkWithManager();
                        if (requiresCar) {
                            lounge.collectKey();
                        } else {
                            outsideWorld.backToWorkByBus();
                        }
                    } else {
                        lounge.talkWithManager();
                        lounge.payForTheService();
                        this.happyCustomer = true;
                        park.collectCar(this.id);
                        outsideWorld.backToWorkByCar();
                    }
                    break;

                case NORMAL_LIFE_WITHOUT_CAR:
                    haveCar = false;
					outsideWorld.goToReception();
                    break;
            }
        }
    }

    public void setCustomerState(CustomerState state) {
		repairShop.reportStatus();
        if (state == this.state) {
            return;
        }
        this.state = state;
    }

    public CustomerState getCustomerState() {
        return this.state;
    }

    public int getCustomerId() {
        return this.id;
    }
	
	public String getCustomerVehicle(){
		if(haveReplacementCar)
			return "R"+Integer.toString(replacementCar);
		else if(haveCar)
			if(id<10)
				return "0"+Integer.toString(id);
			else
				return Integer.toString(id);
		else{
			return "--";
		}
	}
	
	public String requiresReplacementCar(){
		if(requiresCar)
			return "T";
		else 
			return "F";
	}
	
	
	
	public String vehicleRepaired(){
		if(carRepaired)
			return "T";
		else return "F";
	}
}
