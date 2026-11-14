import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Small web server using only the JDK (no frameworks): serves the dashboard and a JSON API. */
public class Server {
    private static final ParkingLot lot = new ParkingLot();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/", ex -> {
            byte[] page = Files.readAllBytes(Paths.get("web", "index.html"));
            send(ex, 200, "text/html; charset=utf-8", page);
        });

        server.createContext("/api/status", ex -> json(ex, 200, lot.statusJson()));

        server.createContext("/api/park", ex -> {
            Map<String, String> p = form(ex);
            try {
                VehicleType type = VehicleType.valueOf(p.getOrDefault("type", ""));
                String slotId = p.getOrDefault("slot", "").trim();
                ParkingSlot slot = slotId.isEmpty()
                        ? lot.park(p.getOrDefault("plate", ""), p.getOrDefault("owner", ""), type)
                        : lot.park(p.getOrDefault("plate", ""), p.getOrDefault("owner", ""), type, slotId);
                json(ex, 200, "{\"message\":\"Parked in slot " + slot.getId() + "\",\"slot\":\"" + slot.getId() + "\"}");
            } catch (ParkingFullException | DuplicateVehicleException e) {
                json(ex, 409, "{\"error\":\"" + JsonSerializable.esc(e.getMessage()) + "\"}");
            } catch (ParkingException e) {
                json(ex, 400, "{\"error\":\"" + JsonSerializable.esc(e.getMessage()) + "\"}");
            } catch (IllegalArgumentException e) {
                json(ex, 400, "{\"error\":\"Please choose a vehicle type\"}");
            }
        });

        server.createContext("/api/exit", ex -> {
            Map<String, String> p = form(ex);
            try {
                long hours = 0;
                String h = p.getOrDefault("hours", "").trim();
                if (!h.isEmpty()) hours = Long.parseLong(h);
                Receipt r = lot.checkout(p.getOrDefault("plate", ""), hours);
                json(ex, 200, r.toJson());
            } catch (VehicleNotFoundException e) {
                json(ex, 404, "{\"error\":\"" + JsonSerializable.esc(e.getMessage()) + "\"}");
            } catch (ParkingException e) {
                json(ex, 400, "{\"error\":\"" + JsonSerializable.esc(e.getMessage()) + "\"}");
            } catch (NumberFormatException e) {
                json(ex, 400, "{\"error\":\"Hours must be a number\"}");
            }
        });

        server.start();
        System.out.println("Smart Parking System running on port " + port);
    }

    private static Map<String, String> form(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            if (pair.isEmpty()) continue;
            String[] kv = pair.split("=", 2);
            map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
        }
        return map;
    }

    private static void json(HttpExchange ex, int code, String body) throws IOException {
        send(ex, code, "application/json; charset=utf-8", body.getBytes(StandardCharsets.UTF_8));
    }

    private static void send(HttpExchange ex, int code, String type, byte[] data) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }
}
