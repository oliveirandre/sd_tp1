package entities;


import repository.RepairShop;
import shared.ICustomerL;
import shared.ICustomerOW;
import shared.ICustomerP;

/**
 *
 * @author Andre e Joao
 */
public class Customer extends Thread {

    private CustomerState state;
	private RepairShop repairShop;

    private final int id;

    private final ICustomerOW outsideWorld;
    private final ICustomerP park;
    private final ICustomerL lounge;

	/**
	 * A boolean that represents if a customer requires a replacement car.
	 */
	public boolean requiresCar = false;
	/**
	 * A boolean that represents if a car has already been repaired.
	 */
	public boolean carRepaired = false;
    private boolean happyCustomer = false;
    private boolean haveReplacementCar = false;
    private int replacementCar;
	private boolean haveCar = true;

	/**
	 * Customer's constructor.
	 * @param outsideWorld
	 * @param park
	 * @param lounge
	 * @param id
	 * @param repairShop
	 */
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
                    haveReplacementCar = true;
					replacementCar = park.findCar();
                    outsideWorld.backToWorkByCar();
                    break;

                case RECEPTION:
                    lounge.queueIn(this.id);
					haveCar=false;
                    if (!carRepaired) {
                        lounge.talkWithManager();
                        if (requiresCar) {
                            haveReplacementCar=true;
							lounge.collectKey();
                        } else {
							haveReplacementCar=false;
							outsideWorld.backToWorkByBus();
                        }
                    } else {
                        lounge.talkWithManager();
                        lounge.payForTheService();
						haveCar=true;
                        this.happyCustomer = true;
                        park.collectCar(this.id);
                        outsideWorld.backToWorkByCar();
                    }
                    break;

                case NORMAL_LIFE_WITHOUT_CAR:
					outsideWorld.goToReception();
                    break;
            }
        }
    }

	/**
	 * Customer's method. Change state of customer and report status to log.
	 * 
	 * @param state state of customer
	 */
	public void setCustomerState(CustomerState state) {
		repairShop.reportStatus();
        if (state == this.state) {
            return;
        }
        this.state = state;
    }

	/**
	 * Customer's method. Retrieves customer's state.
	 * @return customer's state
	 */
	public CustomerState getCustomerState() {
        return this.state;
    }

	/**
	 * Customer's method. Retrieves customer's id.
	 * @return customer's id
	 */
	public int getCustomerId() {
        return this.id;
    }
	
	/**
	 * Method used for log. Retrieves the current car, replacement car
	 * or no car of a customer
	 * @return a String representing the current car of a customer
	 */
	public String getCustomerVehicle(){
		if(!haveReplacementCar && !haveCar)
			return "--";
		else if(!haveReplacementCar)
			if(id<10)
				return "0"+Integer.toString(id);
			else
				return Integer.toString(id);
		else{
			return "R"+Integer.toString(replacementCar);
		}
	}
	
	/**
	 * Method used for log. Retrieves if the customer requires a replacement car.
	 * @return a String representing if the customer requires a replacement car or not
	 */
	public String requiresReplacementCar(){
		if(requiresCar)
			return "T";
		else 
			return "F";
	}
	
	/**
	 * Method used for log. Retrieves if the customer's vehicle has already been repaired.
	 * @return a String that represents if a car has already been repaired
	 */
	public String vehicleRepaired(){
		if(carRepaired)
			return "T";
		else return "F";
	}
}
