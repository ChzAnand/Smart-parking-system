/** Thrown when no free slot is available (inheritance of exceptions). */
public class ParkingFullException extends ParkingException {
    public ParkingFullException(String message) { super(message); }
}
