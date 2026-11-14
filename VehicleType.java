/** Enum: fixed set of vehicle types the parking lot supports. */
public enum VehicleType {
    BIKE("Bike"), CAR("Car"), TRUCK("Truck");

    private final String label;

    VehicleType(String label) { this.label = label; }

    public String getLabel() { return label; }
}
