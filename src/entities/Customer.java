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
                /*case NORMAL_LIFE_WITH_CAR:
                    if (!haveReplacementCar) {
                        requiresCar = outsideWorld.decideOnRepair(this.id, this.state);
                    }
                    outsideWorld.goToRepairShop(this.id, this.state); // nao faz nada
                    setCustomerState(CustomerState.PARK);
                    break;

                case PARK:
                    if (!haveReplacementCar) {
                        park.parkCar(this.id, this.state);
                    } else {
                        park.returnReplacementCar(replacementCar, this.id, this.state);
                        haveReplacementCar = false;
                    }
                    setCustomerState(CustomerState.RECEPTION);
                    break;

                case WAITING_FOR_REPLACE_CAR:
                    haveReplacementCar = true;
                    replacementCar = park.findCar(this.id, this.state);
                    setCustomerState(CustomerState.PARK);
                    outsideWorld.backToWorkByCar(false, replacementCar, this.id); //log mete carro subst
                    setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
                    break;

                case RECEPTION:
                    lounge.queueIn(this.id, this.state);
                    if (!carRepaired) {
                        lounge.talkWithManager(carRepaired, requiresCar);
                        if (requiresCar) {
                            haveReplacementCar = true;
                            setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);
                            carRepaired = lounge.collectKey(this.id, state);
							System.err.println("Customer "+id+" tenho carro substituiçao");
                            
                        } else {
                            haveReplacementCar = false;
							System.err.println("Customer "+id+" vou de autocarro");
							//log mete sem carro
                            carRepaired = outsideWorld.backToWorkByBus(carRepaired, id, state);
                            setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
                        }
						if (carRepaired)
                                setCustomerState(CustomerState.RECEPTION);
                    } else {
                        System.err.println("Customer "+id+" ja vou com carro normal para a minha vida");
						lounge.talkWithManager(true, false);
                        lounge.payForTheService();
                        this.happyCustomer = true;
                        park.collectCar(this.id);
                        
                        
						//log mete carro normal
						System.err.println("Customer "+id+" ja vou com carro normal para a minha vida");
                        carRepaired = outsideWorld.backToWorkByCar(true, -1, this.id);
                        setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
                    }
                    break;

                case NORMAL_LIFE_WITHOUT_CAR:
                    outsideWorld.goToReception(this.id, this.state); //nao faz nada
                    setCustomerState(CustomerState.RECEPTION);
                    break;
                */
                case NORMAL_LIFE_WITH_CAR:
                    //todos entram aqui mas nem todos morrem
                    if(carRepaired) {
                        outsideWorld.backToWorkByCar(carRepaired, -1, this.id);
                        this.happyCustomer = true;
                        break;
                    }
                    else if(haveReplacementCar) {
                        outsideWorld.backToWorkByCar(false, replacementCar, this.id); //log mete carro subst
                        //carRepaired = true;
                    }
                    else {
                        requiresCar = outsideWorld.decideOnRepair(this.id, this.state);
                        requiresCar = false;
                    }
                    outsideWorld.goToRepairShop(this.id, this.state); // nao faz nada
                    setCustomerState(CustomerState.PARK);
                    break;

                case PARK:
                    //.out.println(this.id + " PARK");
                    if(haveReplacementCar && requiresCar) {
                        replacementCar = park.findCar(this.id, this.state);
                        requiresCar = false;
                        setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
                    }
                    else if(haveReplacementCar && !requiresCar) {
                        haveReplacementCar = false;
                        park.returnReplacementCar(replacementCar, this.id, this.state);
                    }
                    if (!haveReplacementCar) {
                        park.parkCar(this.id, this.state);
                    }
                    setCustomerState(CustomerState.RECEPTION);
                    break;

                case WAITING_FOR_REPLACE_CAR:
                    //System.out.println(this.id + " WAITING_FOR_REPLACE_CAR");
                    carRepaired = lounge.collectKey(this.id, this.state);
                    if (carRepaired)
                        setCustomerState(CustomerState.RECEPTION);
                    else {
                        //System.err.println("Customer "+id+" tenho carro substituiçao");
                    }
                    haveReplacementCar = true;
                    //replacementCar = park.findCar(this.id, this.state);
                    setCustomerState(CustomerState.PARK);
                    //outsideWorld.backToWorkByCar(false, replacementCar, this.id); //log mete carro subst
                    //setCustomerState(CustomerState.NORMAL_LIFE_WITH_CAR);
                    break;

                case RECEPTION:
                    //System.out.println(this.id + " RECEPTION");
                    lounge.queueIn(this.id, this.state);
                    if (!carRepaired) {
                        lounge.talkWithManager(carRepaired, requiresCar);
                        if (requiresCar) {
                            //haveReplacementCar = true;
                            setCustomerState(CustomerState.WAITING_FOR_REPLACE_CAR);                            
                        } else {
                            //haveReplacementCar = false;
							//System.err.println("Customer "+id+" vou de autocarro");
							//log mete sem carro
                            //carRepaired = outsideWorld.backToWorkByBus(carRepaired, this.id);
                            setCustomerState(CustomerState.NORMAL_LIFE_WITHOUT_CAR);
                        }
                    } else {
                        //System.err.println("Customer "+id+" ja vou com carro normal para a minha vida");
						lounge.talkWithManager(carRepaired, requiresCar);
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
                    System.err.println(id +" carRpeaird:"+carRepaired);
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
