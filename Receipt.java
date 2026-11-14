import java.time.format.DateTimeFormatter;

/** Bill generated when a vehicle leaves. Immutable (all fields final). */
public class Receipt implements JsonSerializable {
    private final int ticketNo;
    private final Vehicle vehicle;
    private final String slotId;
    private final String entry;
    private final String exit;
    private final long hours;
    private final double fee;

    public Receipt(int ticketNo, Vehicle vehicle, String slotId, java.time.LocalDateTime entry,
                   java.time.LocalDateTime exit, long hours, double fee) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");
        this.ticketNo = ticketNo;
        this.vehicle = vehicle;
        this.slotId = slotId;
        this.entry = entry.format(f);
        this.exit = exit.format(f);
        this.hours = hours;
        this.fee = fee;
    }

    public double getFee() { return fee; }

    @Override
    public String toJson() {
        return "{\"ticket\":" + ticketNo + ",\"plate\":\"" + JsonSerializable.esc(vehicle.getPlateNumber())
                + "\",\"owner\":\"" + JsonSerializable.esc(vehicle.getOwnerName())
                + "\",\"type\":\"" + vehicle.getType().name() + "\",\"slot\":\"" + slotId
                + "\",\"entry\":\"" + entry + "\",\"exit\":\"" + exit + "\",\"hours\":" + hours
                + ",\"rate\":" + vehicle.getRatePerHour()
                + ",\"fee\":" + String.format("%.2f", fee) + "}";
    }
}
