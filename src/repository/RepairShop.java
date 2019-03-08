package repository;

import static repository.RepairShop.N_OF_REPLACEMENT_CARS;

/**
 *
 * @author Andre e Joao
 */

public class RepairShop {
    
    public static final int N_OF_CUSTOMERS = 30;
    public static final int N_OF_MECHANICS = 2;
    public static final int N_OF_MANAGERS = 1;
    public static final int N_OF_REPLACEMENT_CARS = 3;
    public static final int N_OF_PIECES = 3;
    
	Car[] cars = new Car[N_OF_REPLACEMENT_CARS];
	Piece[] pieces = new Piece[N_OF_PIECES];
	
	
	public RepairShop(){
		
		for(int i = 0; i < N_OF_REPLACEMENT_CARS; i++){
			cars[i] = new Car();
		}
		
		for(int i = 0; i < N_OF_PIECES; i++){
			pieces[i] = new Piece();
		}
	}
}
