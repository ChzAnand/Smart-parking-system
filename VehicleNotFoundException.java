/** Thrown when a vehicle that is not parked tries to check out. */
public class VehicleNotFoundException extends ParkingException {
    public VehicleNotFoundException(String message) { super(message); }
}
