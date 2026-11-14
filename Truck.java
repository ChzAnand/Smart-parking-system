/** Truck: Rs.100/hour plus a flat Rs.30 heavy-vehicle charge (overrides calculateFee). */
public class Truck extends Vehicle {
    private static final double HEAVY_CHARGE = 30;

    public Truck(String plate, String owner) {
        super(plate, owner);
    }

    @Override public VehicleType getType()   { return VehicleType.TRUCK; }
    @Override public double getRatePerHour() { return 100; }

    @Override
    public double calculateFee(long hours) {          // METHOD OVERRIDING
        return super.calculateFee(hours) + HEAVY_CHARGE;
    }
}
