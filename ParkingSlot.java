import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One parking slot. ENCAPSULATION: state can only change through park() and free(). */
public class ParkingSlot implements JsonSerializable {
    private final String id;
    private final VehicleType slotType;
    private Vehicle vehicle;                 // null when the slot is free
    private LocalDateTime entryTime;

    public ParkingSlot(String id, VehicleType slotType) {
        this.id = id;
        this.slotType = slotType;
    }

    public boolean isFree()             { return vehicle == null; }
    public String getId()               { return id; }
    public VehicleType getSlotType()    { return slotType; }
    public Vehicle getVehicle()         { return vehicle; }
    public LocalDateTime getEntryTime() { return entryTime; }

    public void park(Vehicle v) {
        this.vehicle = v;
        this.entryTime = LocalDateTime.now();
    }

    public void free() {
        this.vehicle = null;
        this.entryTime = null;
    }

    @Override
    public String toJson() {
        StringBuilder sb = new StringBuilder("{\"id\":\"" + id + "\",\"type\":\"" + slotType.name()
                + "\",\"occupied\":" + !isFree());
        if (!isFree()) {
            sb.append(",\"plate\":\"").append(JsonSerializable.esc(vehicle.getPlateNumber()))
              .append("\",\"owner\":\"").append(JsonSerializable.esc(vehicle.getOwnerName()))
              .append("\",\"since\":\"").append(entryTime.format(DateTimeFormatter.ofPattern("HH:mm"))).append("\"");
        }
        return sb.append("}").toString();
    }
}
