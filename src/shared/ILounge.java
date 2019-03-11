package shared;

/**
 *
 * @author andre and joao
 */
public interface ILounge {
    public void queueIn();
    public void talkWithManager();

    public void payForService();
    public void collectKey(); //define what it is

    //getNextTask está aqui e no supplier store
    public void hetNextTask();

    public void phoneCustomer();
    public void handCarKey();
    public void receivePayment();
    public void registerService();

    public void goToSupplier(); //does he have to be in lounge to go to supplier???

    public void getNextTask();

    public void backToWorkByBus();
}
