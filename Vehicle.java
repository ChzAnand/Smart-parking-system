/**
 * ABSTRACT base class for all vehicles.
 * ABSTRACTION : a plain "Vehicle" cannot be created, only Bike / Car / Truck.
 * ENCAPSULATION: private fields, read through public getters.
 * Implements the Payable interface (fee calculation).
 */
public abstract class Vehicle implements Payable, JsonSerializable {
    private final String plateNumber;
    private final String ownerName;

    public Vehicle(String plateNumber, String ownerName) {
        this.plateNumber = plateNumber;
        this.ownerName = ownerName;
    }

    // Abstract methods: every child class MUST implement these
    public abstract VehicleType getType();
    public abstract double getRatePerHour();

    // Default fee logic. Child classes may OVERRIDE it (polymorphism).
    @Override
    public double calculateFee(long hours) {
        return hours * getRatePerHour();
    }

    public String getPlateNumber() { return plateNumber; }
    public String getOwnerName()   { return ownerName; }

    @Override
    public String toJson() {
        return "{\"plate\":\"" + JsonSerializable.esc(plateNumber) + "\",\"owner\":\""
                + JsonSerializable.esc(ownerName) + "\",\"type\":\"" + getType().name() + "\"}";
    }
}
