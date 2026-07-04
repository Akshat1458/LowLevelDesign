# 🅿️ Parking Lot — LLD Interviewer Review

---

## 1. Process Evaluation (User Flow → Entities → Class Diagram → Code)

Your process is solid and mirrors what interviewers expect:

| Step | Verdict | Comment |
|------|---------|---------|
| User flow first | ✅ Good | Shows you think from the user's perspective before jumping to code |
| Identify core entities | ✅ Good | You picked the right nouns from the user flow |
| Class diagram | ✅ Good | Drawing relationships before coding avoids rework |
| Code implementation | ✅ Good | Code follows the diagram faithfully |

> **Interviewer tip:** Before jumping into the class diagram, explicitly call out which **design patterns** you plan to use and *why*. For example: *"I'll use Strategy for payments because the payment method can vary at runtime, and Factory to encapsulate the creation logic."* This signals design maturity.

---

## 2. Design Document Review (`design.md`)

### 2.1 User Flow — Good, but Gaps

Your 9-step flow covers the happy path well. An interviewer would probe:

- **What happens if the user loses the ticket?** There's no ticket-ID-based lookup.
- **What if the same vehicle tries to enter twice?** No duplicate vehicle check.
- **What if payment fails?** The flow assumes payment always succeeds.
- **Entry/Exit gates?** Real parking lots have multiple entry/exit points with concurrency concerns.

> ⚠️ **In an interview, proactively mention at least 1–2 edge cases even if you don't implement them. It shows you think beyond the happy path.**

### 2.2 Core Entities — Missing One Key Entity

You identified 7 entities: `Vehicle`, `ParkingLot`, `Floors`, `ParkingSlot`, `Ticket`, `ParkingLotManager`, `Payment`.

**Missing:**
- **`PricingCalculator`** — You built it in code but didn't mention it as an entity. It's a first-class concern in a parking system.
- **`EntryGate` / `ExitGate`** — Not strictly required for a basic design, but mentioning them shows awareness of real-world modeling.

### 2.3 Class Diagram — Well Structured, Minor Issues

**Strengths:**
- Correct use of composition (`*--`) for ParkingLot→Floor→Slot.
- Strategy pattern for Payment is clearly shown.
- Inheritance for Vehicle and Slot subtypes is correct.

**Issues:**

| Issue | Detail |
|-------|--------|
| `PricingCalculator` missing from diagram | It exists in code but not in the design document. Diagram should be the single source of truth. |
| `PaymentModeFactory` missing from diagram | Same — exists in code, absent in design. |
| `Slot.type` shown as `String` | Diagram says `-String type` but code correctly uses `TypeOfVehicle` enum. Diagram is stale. |
| `BikeSlot` type inconsistency | Diagram says `Type = Bike` (lowercase), `CarSlot` says `Type = CAR` (uppercase). Use consistent casing. |
| `Payment.modeOfPayment` shown as `String` | Diagram says `String` but code correctly uses `PaymentMode` enum. |
| `ParkingLotManager.createTicket()` in diagram | The code method is named `generateTicket()`. Keep naming consistent. |

> ⚠️ **In an interview, if your diagram and code diverge, it raises a red flag. The interviewer may think you designed on-the-fly without a plan. Always keep the diagram as the source of truth.**

---

## 3. Design Pattern Usage

| Pattern | Where | Verdict |
|---------|-------|---------|
| **Strategy** | `PaymentStrategy` → `CashPaymentStrategy`, `UPIPaymentStrategy` | ✅ Correctly applied |
| **Factory** | `PaymentModeFactory` | ✅ Correctly applied |
| **Inheritance** | `Vehicle` → `Car`/`Bike`, `Slot` → `CarSlot`/`BikeSlot` | ⚠️ See below |

### 3.1 CarSlot / BikeSlot — Inheritance is Overkill Here

`CarSlot` and `BikeSlot` do **nothing** except call `super(TypeOfVehicle.CAR)` and `super(TypeOfVehicle.BIKE)`. They add zero behavior.

```java
// CarSlot.java — the entire class
public class CarSlot extends Slot {
    public CarSlot(){
        super(TypeOfVehicle.CAR);
    }
}
```

**Interviewer question:** *"What does `CarSlot` give you that `new Slot(TypeOfVehicle.CAR)` doesn't?"*

**Recommendation:** Either:
1. **Remove the subclasses** and use `new Slot(TypeOfVehicle.CAR)` directly in `Floor`, OR
2. **Add slot-specific behavior** (e.g., `CarSlot` has a different size, `BikeSlot` allows 2 bikes per slot) to justify the inheritance.

The same applies to `Car` and `Bike` — they only call `super()` with a different enum value.

> Using inheritance *just to set an enum value* violates YAGNI (You Aren't Gonna Need It). In an interview, the interviewer will ask you to justify each class.

---

## 4. Code-Level Bugs & Issues

### 🐛 Bug 1: `NullPointerException` in `ParkingLot.parkVehicle()`

```java
// ParkingLot.java:37-41
public Slot parkVehicle(Vehicle vehicle){
    Slot slot = findVacantSlot(vehicle.getTypeOfVehicle());
    slot.parkVehicle(vehicle);  // 💥 NPE if no slot available!
    return slot;
}
```

`findVacantSlot()` can return `null`. You check availability in `ParkingLotManager.handleEntry()`, but there's a **race condition**: between `checkAvailableSlot()` and `parkVehicle()`, another thread could take the last slot.

**Fix:** Add a null check inside `parkVehicle()`, or make the check-and-park atomic.

---

### 🐛 Bug 2: `Slot.unparkVehicle()` doesn't clear the vehicle reference

```java
// Slot.java:29-31
public void unparkVehicle(){
    occupied = false;
    // ❌ Missing: this.vehicle = null;
}
```

The slot is marked as free, but still holds a reference to the old vehicle. This causes:
- **Memory leak** — old Vehicle objects can't be GC'd.
- **Stale data** — if anyone reads `slot.vehicle`, they get the previous vehicle.

---

### 🐛 Bug 3: `Scanner` leak in `ParkingLotManager.handleExit()`

```java
// ParkingLotManager.java:46
Scanner scanner = new Scanner(System.in);
int choice = scanner.nextInt();
// ❌ scanner is never closed
```

A new `Scanner` is created on every exit, wrapping `System.in`. Closing it would close `System.in` too, which is also problematic. **More fundamentally, a domain class should not do I/O** — see the SRP violation below.

---

### 🐛 Bug 4: `PaymentModeFactory` can return `null`

```java
// PaymentModeFactory.java:14-15
    }
    return null;  // 💥 If a new PaymentMode enum value is added, this silently returns null
```

**Fix:** Throw an `IllegalArgumentException` in the default case, just like you correctly did in `PricingCalculator.getRate()`.

---

---

## 5. SOLID Principle Violations

### 🔴 SRP Violation — `ParkingLotManager.handleExit()` does too much

This single method:
1. Sets the exit time on the ticket
2. Unparks the vehicle from the slot
3. Calculates the price
4. **Prints to console** (UI concern)
5. **Reads user input via Scanner** (UI concern)
6. Creates a payment strategy via factory
7. Collects payment
8. Sets payment on ticket

**This is the biggest issue an interviewer would flag.** Business logic and UI/IO are mixed together.

**Fix:** Extract the I/O into the `Main` class (or a controller layer). `handleExit()` should accept the `PaymentMode` as a parameter:

```java
// Clean version
public double handleExit(Ticket ticket) {
    ticket.setExitTime(LocalDateTime.now());
    ticket.getSlot().unparkVehicle();
    return PricingCalculator.calculatePrice(ticket);
}

public void processPayment(Ticket ticket, PaymentMode mode, double amount) {
    PaymentStrategy strategy = PaymentModeFactory.getPaymentStrategy(mode);
    Payment payment = strategy.collectPayment(amount);
    ticket.setPayment(payment);
}
```

### 🟡 OCP Consideration — Adding a new vehicle type

If you add `TRUCK`, you'd need to:
1. Add to `TypeOfVehicle` enum ✅
2. Create `Truck extends Vehicle` (if keeping subclasses)
3. Create `TruckSlot extends Slot` (if keeping subclasses)
4. Modify `Floor` constructor to accept truck slot count
5. Modify `PricingCalculator.getRate()` switch
6. Modify `ParkingLot` constructor / floor config

That's a lot of places to touch. Consider a more data-driven approach for slot configuration.

---

## 6. Concurrency — Not Addressed

> 🚨 **In an interview for a Senior / SDE-2+ role, the interviewer will ask about concurrency. Your current design has zero thread safety.**

**Key race condition:**
```
Thread A: checkAvailableSlot(CAR) → true (1 slot left)
Thread B: checkAvailableSlot(CAR) → true (same slot)
Thread A: parkVehicle(carA) → parks in slot
Thread B: parkVehicle(carB) → 💥 overwrites carA in the same slot, or NPE
```

**What to mention in an interview (even if you don't implement it):**
- Synchronize `findVacantSlot()` + `parkVehicle()` as an atomic operation
- Or use `ConcurrentHashMap` / `ReentrantLock` per slot
- Or use optimistic locking with `compareAndSet`

---

## 7. Missing Features an Interviewer May Ask About

| Feature | Difficulty | Likely to be asked? |
|---------|-----------|-------------------|
| **Ticket ID / lookup by ID** | Easy | Very likely |
| **Floor number / slot number** | Easy | Very likely — currently slots have no identity |
| **Multiple entry/exit gates** | Medium | Likely for Senior roles |
| **Capacity display per floor** | Easy | Commonly asked |
| **Handicapped / EV slots** | Easy | Shows extensibility thinking |
| **Reservation system** | Hard | Unlikely in 45 min |

---

## 8. Overall Scorecard

| Criteria | Score | Notes |
|----------|-------|-------|
| **Process & Approach** | 8/10 | Good top-down flow. Mention design patterns upfront. |
| **Design Document** | 6/10 | Diagram diverges from code; missing classes. |
| **Entity Identification** | 7/10 | Core entities present; missed PricingCalculator. |
| **Design Patterns** | 7/10 | Strategy + Factory are correct. Inheritance is unjustified for Car/Bike subclasses. |
| **Code Correctness** | 5/10 | NPE risk, vehicle reference leak, Scanner leak, null return from Factory. |
| **SOLID Principles** | 5/10 | Major SRP violation in handleExit; OCP could be better. |
| **Concurrency** | 2/10 | Not addressed at all. |
| **Extensibility** | 6/10 | Patterns are in place but slots/vehicles lack identity. |
| **Overall** | **6/10** | Good foundation, needs fixes to be interview-ready. |

---

## 9. Priority Fix List

1. **🔴 Fix SRP** — Move I/O out of `ParkingLotManager.handleExit()`
2. **🔴 Fix NPE** — Null-check in `ParkingLot.parkVehicle()`
3. **🔴 Fix vehicle leak** — Set `this.vehicle = null` in `unparkVehicle()`
4. **🟡 Sync diagram with code** — Add `PricingCalculator`, `PaymentModeFactory`; fix types
5. **🟡 Throw exception** — Replace `return null` in `PaymentModeFactory`
6. **🟢 Add identifiers** — Slot number, floor number, ticket ID
7. **🟢 Consider removing** — `CarSlot`/`BikeSlot`/`Car`/`Bike` subclasses unless you add behavior
8. **🟢 Mention concurrency** — Even if you don't implement it, call it out in your design
