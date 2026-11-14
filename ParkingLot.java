import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages all slots, parking, checkout and billing.
 * POLYMORPHISM (overloading): park() has two versions.
 * POLYMORPHISM (overriding at run time): vehicle.calculateFee() runs the Bike/Car/Truck version.
 * ENCAPSULATION: slots, history and revenue are private.
 */
public class ParkingLot {
    private final List<ParkingSlot> slots = new ArrayList<>();
    private final LinkedList<Receipt> history = new LinkedList<>();
    private double revenue = 0;
    private int ticketCounter = 1000;
    private int vehiclesServed = 0;

    public ParkingLot() {
        for (int i = 1; i <= 8; i++) slots.add(new ParkingSlot("B" + i, VehicleType.BIKE));
        for (int i = 1; i <= 8; i++) slots.add(new ParkingSlot("C" + i, VehicleType.CAR));
        for (int i = 1; i <= 4; i++) slots.add(new ParkingSlot("T" + i, VehicleType.TRUCK));
    }

    // Overloaded method 1: auto-assign the first free slot of the right type
    public synchronized ParkingSlot park(String plate, String owner, VehicleType type)
            throws ParkingException {
        return park(plate, owner, type, null);
    }

    // Overloaded method 2: user chooses a particular slot
    public synchronized ParkingSlot park(String plate, String owner, VehicleType type, String slotId)
            throws ParkingException {
        if (plate == null || plate.isBlank()) throw new ParkingException("Vehicle number is required");
        if (owner == null || owner.isBlank()) throw new ParkingException("Owner name is required");
        plate = plate.trim().toUpperCase();            // store plate numbers in capitals
        if (findByPlate(plate) != null) {
            throw new DuplicateVehicleException("Vehicle " + plate + " is already parked");
        }

        ParkingSlot chosen = null;
        for (ParkingSlot s : slots) {
            if (s.getSlotType() == type && s.isFree() && (slotId == null || s.getId().equals(slotId))) {
                chosen = s;
                break;
            }
        }
        if (chosen == null) {
            throw new ParkingFullException(slotId == null
                    ? "No free " + type.getLabel() + " slot available"
                    : "Slot " + slotId + " is not available for a " + type.getLabel());
        }

        Vehicle v;                                    // parent reference, child object (upcasting)
        switch (type) {
            case BIKE:  v = new Bike(plate, owner);  break;
            case CAR:   v = new Car(plate, owner);   break;
            default:    v = new Truck(plate, owner); break;
        }
        chosen.park(v);
        return chosen;
    }

    /** hoursOverride: pass a value > 0 to simulate a longer stay (used for demo). */
    public synchronized Receipt checkout(String plate, long hoursOverride) throws ParkingException {
        ParkingSlot slot = findByPlate(plate);
        if (slot == null) throw new VehicleNotFoundException("Vehicle " + plate + " is not parked here");

        Vehicle v = slot.getVehicle();
        LocalDateTime exit = LocalDateTime.now();
        long minutes = Duration.between(slot.getEntryTime(), exit).toMinutes();
        long hours = hoursOverride > 0 ? hoursOverride : Math.max(1, (long) Math.ceil(minutes / 60.0));

        double fee = v.calculateFee(hours);           // RUNTIME POLYMORPHISM
        Receipt r = new Receipt(++ticketCounter, v, slot.getId(), slot.getEntryTime(), exit, hours, fee);

        slot.free();
        revenue += fee;
        vehiclesServed++;
        history.addFirst(r);
        if (history.size() > 10) history.removeLast();
        return r;
    }

    private ParkingSlot findByPlate(String plate) {
        for (ParkingSlot s : slots) {
            if (!s.isFree() && s.getVehicle().getPlateNumber().equalsIgnoreCase(plate.trim())) return s;
        }
        return null;
    }

    public synchronized String statusJson() {
        int occupied = 0;
        for (ParkingSlot s : slots) if (!s.isFree()) occupied++;

        StringBuilder sb = new StringBuilder("{\"stats\":{");
        sb.append("\"total\":").append(slots.size()).append(",\"occupied\":").append(occupied)
          .append(",\"available\":").append(slots.size() - occupied)
          .append(",\"revenue\":").append(String.format("%.2f", revenue))
          .append(",\"served\":").append(vehiclesServed).append(",\"byType\":[");
        boolean first = true;
        for (VehicleType t : VehicleType.values()) {
            int total = 0, occ = 0;
            for (ParkingSlot s : slots) {
                if (s.getSlotType() == t) { total++; if (!s.isFree()) occ++; }
            }
            if (!first) sb.append(",");
            first = false;
            sb.append("{\"type\":\"").append(t.name()).append("\",\"label\":\"").append(t.getLabel())
              .append("\",\"total\":").append(total).append(",\"occupied\":").append(occ).append("}");
        }
        sb.append("]},\"slots\":[");
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(slots.get(i).toJson());
        }
        sb.append("],\"history\":[");
        for (int i = 0; i < history.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(history.get(i).toJson());
        }
        return sb.append("]}").toString();
    }
}
