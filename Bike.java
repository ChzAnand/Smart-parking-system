/** INHERITANCE: Bike IS-A Vehicle. */
public class Bike extends Vehicle {
    public Bike(String plate, String owner) {
        super(plate, owner);                  // constructor chaining with super()
    }

    @Override public VehicleType getType()   { return VehicleType.BIKE; }
    @Override public double getRatePerHour() { return 20; }
}
