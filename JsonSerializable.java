/** Interface: classes that can convert themselves to JSON implement this. */
public interface JsonSerializable {
    String toJson();

    // static helper method inside an interface (Java 8+)
    static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
