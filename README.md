# Parcel Delivery System

A simple Java console program for the `polymorphism-exception-patterns-exercise` assignment. It uses plain Java, with no frameworks or external libraries.

## Run the program

Install a Java Development Kit (JDK 8 or newer). From the repository folder, run:

```sh
javac -d out src/*.java
java -cp out Main
```

In Windows PowerShell, use:

```powershell
New-Item -ItemType Directory -Force out
javac -d out (Get-ChildItem src/*.java).FullName
java -cp out Main
```

Choose an option from the menu:

1. Add Parcel
2. View All Parcels
3. Change Delivery Service
4. Mark as Delivered
5. Exit

Parcels stay in memory while the program runs. They are cleared when the program exits.

## Classes and patterns

| Requirement | How the program demonstrates it |
| --- | --- |
| Polymorphism | `Parcel` is abstract. `DocumentParcel`, `BoxParcel`, and `FragileParcel` override methods for their type, chargeable weight, or base fee. The menu and manager use `Parcel` references. |
| Inheritance | `FragileParcel` extends `BoxParcel` and reuses its dimensions and chargeable-weight calculation. |
| Exception handling | `ParcelException` handles invalid parcel details and rejected operations. `NumberFormatException` handles invalid numeric input. The menu catches exceptions and displays understandable messages. |
| Singleton | `ParcelManager` has a private constructor and one shared instance accessed through `getInstance()`. |
| Strategy | `DeliveryService` defines the fee operation. `StandardDelivery`, `ExpressDelivery`, and `SameDayDelivery` implement different fee rules. A pending parcel can switch strategies. |

`Main.java` handles the menu and input. Input loops use `do-while`, and early returns keep conditions simple without nested `if` statements or `while (true)`.

## Parcel rules

All parcels require a unique tracking number matching `ABC` and exactly four digits, a non-empty receiver name, and a finite weight greater than zero. New parcels are Pending.

| Type | Additional details | Maximum actual weight | Chargeable weight | Base fee |
| --- | --- | --- | --- | --- |
| Document | 1-100 pages | 0.5 kg | Actual weight | Php 50 |
| Box | Positive whole-number length, width, height in cm | 50 kg | Larger of actual weight and volume / 3500, rounded up | Php 40 per chargeable kg |
| Fragile | Box dimensions and a non-empty handling note | 30 kg | Same as Box | Box base fee + Php 100 |

| Service | Delivery fee |
| --- | --- |
| Standard | Base fee |
| Express | Base fee x 1.5 |
| Same Day | Base fee x 2 + Php 100; chargeable weight must be at most 10 kg |

When adding a parcel, an unavailable Same Day service prompts for another choice. When changing a service, an unavailable Same Day selection keeps the previous service and fee. Delivered parcels cannot change services.

## Example

Add a Fragile parcel with tracking number `ABC0001`, receiver `Juan Dela Cruz`, weight `1.8`, dimensions `40`, `20`, `10`, handling note `Handle with care`, and Express delivery.

- Volumetric weight: 8000 / 3500 = approximately 2.29 kg
- Chargeable weight: 3 kg
- Base fee: (40 x 3) + 100 = Php 220
- Express delivery fee: 220 x 1.5 = **Php 330.00**

## Run the checks

The checks use ordinary Java methods and need no testing framework:

```sh
javac -d out src/*.java tests/ParcelSystemTest.java
java -cp out ParcelSystemTest
```

In Windows PowerShell:

```powershell
javac -d out (Get-ChildItem src/*.java).FullName tests/ParcelSystemTest.java
java -cp out ParcelSystemTest
```
