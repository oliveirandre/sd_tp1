package repository;

/**
 *
 * @author Andre and Joao
 */
public class Car {

    private int id = 0;
    private static int count = 0;

    public Car() {
        id = count++;
    }

    public int getId() {
        return id;
    }
}
