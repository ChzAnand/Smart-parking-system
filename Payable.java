/** Interface: anything that can calculate a fee for the hours used. */
public interface Payable {
    double calculateFee(long hours);
}
