# Smart Parking Management System (Java)

A campus parking web app with a live dashboard. **Java backend** (OOP classes + built-in HTTP server) and an HTML dashboard.
Users park bikes, cars and trucks, see free/occupied slots live, and get an automatic bill at check-out.

## Features
- 20 slots: 8 bike, 8 car, 4 truck. Slots turn red when occupied
- Park a vehicle (auto-assigns the first free slot of its type)
- Check-out with automatic fee calculation and a printed receipt
- Live stats: total, occupied, available, revenue, occupancy by type
- Error handling: parking full, duplicate vehicle, vehicle not found, blank input

## Fee rules
| Vehicle | Rate | Extra rule |
|---|---|---|
| Bike | Rs.20 / hour | - |
| Car | Rs.50 / hour | 10% discount for 5+ hours |
| Truck | Rs.100 / hour | + Rs.30 flat heavy-vehicle charge |

Billed per hour, minimum 1 hour.

## OOP Concepts Used (where in the code)
| Concept | Where |
|---|---|
| **Abstraction** | `Vehicle` is an abstract class with abstract methods `getType()` and `getRatePerHour()` |
| **Inheritance** | `Bike`, `Car`, `Truck` extend `Vehicle`. Also `ParkingFullException`, `VehicleNotFoundException`, `DuplicateVehicleException` extend `ParkingException` |
| **Polymorphism: overriding (run time)** | `Car` and `Truck` override `calculateFee()`; `ParkingLot` calls `vehicle.calculateFee()` on a `Vehicle` reference |
| **Polymorphism: overloading (compile time)** | `ParkingLot.park(plate, owner, type)` and `park(plate, owner, type, slotId)` |
| **Encapsulation** | Private fields with getters in `Vehicle`, `ParkingSlot`, `Receipt`; slots/revenue private in `ParkingLot` |
| **Interface** | `Payable` (fee calculation) and `JsonSerializable`, implemented by `Vehicle` |
| **Enum** | `VehicleType` (BIKE, CAR, TRUCK) |
| **Exception handling** | Custom checked exceptions thrown in `ParkingLot`, caught in `Server` and shown to the user |
| **Constructor chaining** | `super(plate, owner)` in `Bike`, `Car`, `Truck` |
| **Upcasting** | `Vehicle v = new Car(...)` in `ParkingLot.park()` |
| **Collections** | `ArrayList` (slots), `LinkedList` (receipt history), `HashMap` (request data) |
| **Synchronization** | `synchronized` methods so two users cannot take the same slot |

## Run locally
```
javac -d out src/*.java
java -cp out Server
```
Open http://localhost:8080 (needs JDK 17+).

## Deploy on Render (free)
1. Push this folder to a GitHub repo (Dockerfile must be at the top level).
2. render.com > New > Web Service > connect the repo.
3. Runtime: Docker, Instance type: Free > Create Web Service.
4. Wait for the build; open the public URL.

## Structure
```
src/  Vehicle, Bike, Car, Truck, VehicleType, Payable, JsonSerializable,
      ParkingSlot, Receipt, ParkingLot, ParkingException (+3 child exceptions), Server
web/  index.html (dashboard)
Dockerfile
```
