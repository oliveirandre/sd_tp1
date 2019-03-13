package shared;

/**
 *
 * @author andre and joao
 */
public interface ILounge {
    public void talkWithManager();
    public void talkWithCustomer();
    public void phoneCustomer();
    public void payForService();
    public void readThePaper();
    public void findCar();
    public void collectKey(); //define what it is
    public void appraiseSit();
    //getNextTask está aqui e no supplier store
    public void handCarKey();
    public void receivePayment();
    public void registerService();

    public void goToSupplier(); //does he have to be in lounge to go to supplier???

    public void getNextTask();

    public void backToWorkByBus();
    public void collectCar();
}
