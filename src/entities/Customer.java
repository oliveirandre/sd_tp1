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

	private final int id;

	private final ICustomerOW outsideWorld;
	private final ICustomerP park;
	private final ICustomerL lounge;

	/**
	 * A boolean that represents if a customer requires a replacement car.
	 */
	private boolean requiresCar = false;
	/**
	 * A boolean that represents if a car has already been repaired.
	 */
	private boolean carRepaired = false;
	private boolean happyCustomer = false;
	private boolean haveReplacementCar = false;
	private int replacementCar;

	/**
	 * Customer's constructor.
	 *
	 * @param outsideWorld
	 * @param park
	 * @param lounge
	 * @param id
	 */
	public Customer(ICustomerOW outsideWorld, ICustomerP park, ICustomerL lounge, int id) {
		this.outsideWorld = outsideWorld;
		this.park = park;
		this.lounge = lounge;
		this.id = id;
	}

	@Override
	public void run() {
		this.setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
		while (!this.happyCustomer) {
			switch (this.state) {
				case NORMAL_LIFE_WITH_CAR:
					//todos entram aqui mas nem todos morrem
					if (carRepaired) {
						outsideWorld.backToWorkByCar(carRepaired, -1, id, state);
						happyCustomer = true;
						break;
					} else if (haveReplacementCar) {
						requiresCar = false;
						carRepaired = outsideWorld.backToWorkByCar(false, replacementCar, id, state); //log mete carro subst

						//carRepaired = true;
						setCustomerState(CustomerState.PARK);
						break;
					} else {
						requiresCar = outsideWorld.decideOnRepair(id, state);
						//requiresCar = false;
						//requiresCar=true;
					}
					//outsideWorld.goToRepairShop(this.id, this.state); // nao faz nada
					setCustomerState(CustomerState.PARK);
					break;

				case PARK:
					//.out.println(this.id + " PARK");
					if (haveReplacementCar && carRepaired) {
						haveReplacementCar = false;
						park.returnReplacementCar(replacementCar, id, state);
						outsideWorld.backToWorkByCar(false, -1, id, state);
						//System.out.println(id+ " retornei carro "+replacementCar);
						setCustomerState(CustomerState.RECEPTION);
						break;
					}
					park.parkCar(id, state);

					setCustomerState(CustomerState.RECEPTION);
					break;

				case WAITING_FOR_REPLACE_CAR:
					//System.out.println(id + " WAITING_FOR_REPLACE_CAR");

					carRepaired = lounge.collectKey(id, state);
					//System.err.println(id+" collected key");
					if (carRepaired) {
						//System.err.println("this");
						outsideWorld.backToWorkByCar(carRepaired, -1, id, state);
						setCustomerState(CustomerState.RECEPTION);
						break;
					}
					replacementCar = lounge.getCarReplacementId(id);
					
					park.findCar(id, state, replacementCar);

					//System.out.println("replacementCar:"+replacementCar);
					//outsideWorld.backToWorkByCar(false, replacementCar, id); //log mete carro subst
					haveReplacementCar = true;
					//setCustomerState(CustomerState.PARK);
					setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
					break;

				case RECEPTION:
					//System.out.println(this.id + " RECEPTION");
					lounge.queueIn(id, state);
					lounge.talkWithManager(carRepaired, requiresCar);
					if (!carRepaired) {
						if (requiresCar) {
							//haveReplacementCar = true;
							setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);
						} else {
							setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
						}
					} else {
						//System.err.println("Customer "+id+" ja vou com carro normal para a minha vida");

						lounge.payForTheService();
						//park.collectCar(this.id);
						//log mete carro normal
						//System.err.println("Customer "+id+" ja vou com carro normal para a minha vida");
						//carRepaired = outsideWorld.backToWorkByCar(true, -1, this.id);
						//this.happyCustomer = true;
						setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
					}
					break;

				case NORMAL_LIFE_WITHOUT_CAR:
					//System.out.println(this.id + " NORMAL_LIFE_WITHOUT_CAR");
					//PROBLEMA AQUI, ELES NÃO VOLTAM TODOS DO OUTSIDE WORLD
					carRepaired = outsideWorld.backToWorkByBus(carRepaired, id, state);
					//outsideWorld.goToReception(this.id, this.state); //nao faz nada
					//System.err.println(this.id+"checked");
					//System.err.println(id + " carRpeaird:" + carRepaired);
					setCustomerState(CustomerState.RECEPTION);
					break;
			}
		}
	}

	/**
	 * Customer's method. Change state of customer and report status to log.
	 *
	 * @param state state of customer
	 */
	private void setCustomerState(CustomerState state) {
		if (state == this.state) {
			return;
		}
		this.state = state;
	}

	/**
	 * Customer's method. Retrieves customer's state.
	 *
	 * @return customer's state
	 */
	private CustomerState getCustomerState() {
		return this.state;
	}

	/**
	 * Customer's method. Retrieves customer's id.
	 *
	 * @return customer's id
	 */
	private int getCustomerId() {
		return this.id;
	}

	/**
	 * Method used for log. Retrieves the current car, replacement car or no car
	 * of a customer
	 *
	 * @return a String representing the current car of a customer
	 */
	public String getCustomerVehicle() {
		/*if (!haveReplacementCar && !haveCar) {
            return "--";
        } else if (!haveReplacementCar) {
            if (id < 10) {
                return "0" + Integer.toString(id);
            } else {
                return Integer.toString(id);
            }
        } else {
            return "R" + Integer.toString(replacementCar);
        }*/
		return "";
	}

	/**
	 * Method used for log. Retrieves if the customer requires a replacement
	 * car.
	 *
	 * @return a String representing if the customer requires a replacement car
	 * or not
	 */
	public String requiresReplacementCar() {
		if (requiresCar) {
			return "T";
		} else {
			return "F";
		}
	}

	/**
	 * Method used for log. Retrieves if the customer's vehicle has already been
	 * repaired.
	 *
	 * @return a String that represents if a car has already been repaired
	 */
	public String vehicleRepaired() {
		if (carRepaired) {
			return "T";
		} else {
			return "F";
		}
	}
}
