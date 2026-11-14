/** Thrown when the same vehicle is already parked. */
public class DuplicateVehicleException extends ParkingException {
    public DuplicateVehicleException(String message) { super(message); }
}
