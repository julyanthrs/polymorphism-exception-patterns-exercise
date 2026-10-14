# Parcel Delivery System

A beginner-friendly Java console project for OOPROG. It stores parcels in memory using one `ArrayList`; data is cleared when the program exits.

## Requirements and running

Install a Java Development Kit (JDK 8 or newer). Open a terminal in this repository's folder:

```sh
javac *.java
java Main
```

Choose **Add Parcel**, **View All Parcels**, **Change Delivery Service**, **Mark as Delivered**, or **Exit**. Invalid fields are requested again without losing previously entered details.

## File structure

```text
polymorphism-exception-patterns-exercise/
├── Main.java
├── Parcel.java
├── Document.java
├── Box.java
├── Fragile.java
├── ParcelManager.java
├── DeliveryStrategy.java
├── StandardDelivery.java
├── ExpressDelivery.java
├── SameDayDelivery.java
├── README.md
└── tests/
    └── ParcelSystemTest.java
```

All application classes are in one folder and use the default package. The optional test file runs separately.

## OOP concepts

| Concept | Implementation |
| --- | --- |
| Encapsulation | Private fields and getters protect parcel details. Tracking, receiver, weight, and additional attributes cannot be changed after construction. |
| Inheritance | `Document` and `Box` extend `Parcel`. `Fragile` extends `Box`. |
| Polymorphism | `Parcel` references call overridden type, chargeable-weight, and base-fee methods for each parcel type. |
| Exception handling | `NumberFormatException` handles numeric conversion errors. `IllegalArgumentException` handles invalid details and rejected operations. The menu catches errors and shows readable messages. |
| Singleton | `ParcelManager` has one static instance, a private constructor, and `getInstance()`. It handles adding, searching, service changes, and deliveries. |
| Strategy | `DeliveryStrategy.calculateFee(double baseFee)` is implemented by the three delivery service classes. Pending parcels can change their strategy. |

## Rules

Every parcel needs a unique tracking number such as `ABC0001`, a non-empty receiver name, and a positive finite weight. New parcels are Pending.

| Type | Additional fields | Maximum actual weight | Chargeable weight | Base fee |
| --- | --- | --- | --- | --- |
| Document | 1-100 pages | 0.5 kg | Actual weight | Php 50 |
| Box | Positive integer length, width, height in cm | 50 kg | `Math.ceil(Math.max(actual weight, volume / 3500))` | Php 40 per chargeable kg |
| Fragile | Box dimensions and a non-empty handling note | 30 kg | Same as Box | Box base fee + Php 100 |

Standard uses the base fee. Express multiplies it by 1.5. Same Day doubles it and adds Php 100, and requires a chargeable weight of at most 10 kg.

The shared validation in `Parcel` rejects unavailable Same Day delivery before changing the strategy or fee. Adding then asks for another service. A rejected service change returns to the menu and keeps the previous service and fee. Delivered parcels cannot change services or be marked Delivered again.

## Example

Choose Add Parcel, then Fragile. Enter `ABC0001`, receiver `Juan Dela Cruz`, weight `1.8`, dimensions `40`, `20`, `10`, handling note `Handle with care`, and Express service.

The volumetric weight is 8000 / 3500, approximately 2.29 kg. Chargeable weight rounds up to 3 kg. The base fee is (40 x 3) + 100 = Php 220. Express delivery costs **Php 330.00**.

## Optional checks

The checks use ordinary Java methods with no testing library:

```sh
javac -d out *.java tests/ParcelSystemTest.java
java -cp out ParcelSystemTest
```
