/** Car: Rs.50/hour, and 10% discount if parked for 5 hours or more (overrides calculateFee). */
public class Car extends Vehicle {
    public Car(String plate, String owner) {
        super(plate, owner);
    }

    @Override public VehicleType getType()   { return VehicleType.CAR; }
    @Override public double getRatePerHour() { return 50; }

    @Override
    public double calculateFee(long hours) {          // METHOD OVERRIDING
        double fee = super.calculateFee(hours);
        return hours >= 5 ? fee * 0.90 : fee;
    }
}
