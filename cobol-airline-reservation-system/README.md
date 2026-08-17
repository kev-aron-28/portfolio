# Airline Reservation System
A learning-oriented Airline Reservation System built on IBM z/OS using classic mainframe technologies.

This project simulates a simplified airline reservation application to gain hands-on experience with COBOL, CICS, DB2, JCL and common z/OS utilities. The objective is to follow development practices commonly found in enterprise mainframe applications while keeping the scope manageable for a standalone educational project.

This is **not** intended to replicate commercial airline systems such as Sabre or Amadeus, but rather to demonstrate the architecture, technologies and workflows used in traditional IBM Mainframe development.

---

# Objectives

- Learn Enterprise COBOL application development
- Build online applications with CICS
- Integrate COBOL with DB2
- Process batch workloads with JCL
- Work with VSAM datasets
- Use common IBM utilities
- Organize a medium-sized mainframe application
- Practice modular design and reusable COPYBOOKs

# Technology Stack

- Enterprise COBOL
- CICS
- DB2
- JCL
- VSAM
- DFSORT
- IDCAMS
- IEBGENER
- IEBCOPY
- Hercules
- z/OS 1.11

# Functional Scope

The system consists of four core business modules.

## Passenger Management

Manage passenger information.

Features

- Register passenger
- Update passenger information
- Search passenger
- View passenger profile

---

## Flight Management

Manage available flights.

Features

- Create flight
- Update flight
- Cancel flight
- Change flight status
- View flight information

---

## Reservation Management

Handle flight reservations.

Features

- Create reservation
- Modify reservation
- Cancel reservation
- Assign seats
- View reservation

---

## Check-in

Passenger check-in process.

Features

- Check in passenger
- Confirm assigned seat
- Register checked baggage
- Complete boarding process

---

# Database

The project uses DB2 as the primary database.

Main tables include:

- PASSENGER
- FLIGHT
- AIRCRAFT
- AIRPORT
- RESERVATION
- SEAT
- CHECKIN

---

# Batch Processing

Several batch jobs simulate daily airline operations.

## Flight Closing

- Close completed flights
- Update flight status
- Generate operational summary

## Daily Statistics

Generate reports including:

- Flights processed
- Passenger count
- Seat occupancy
- Flight cancellations


# System views

1. Passenger
Stores customer information.
Screens:
- Create Passenger
- Search Passenger
- Update Passenger
- View Passenger

2. Aircraft
Stores every aircraft available in the airline
Screens:
- Create Aircraft
- Search Aircraft
- Update Aircraft

3. AirPort
Stores airports that flights can use.
Screens:
- Create Airport
- Search Airport
- Update Airport

4. Flight
Creates a scheduled flight.
A flight needs:
    1. One aircraft
    2. One origin airport
    3.One destination airport
Screens:
- Create Flight
- Search Flight
- Update Flight
- Cancel Flight

5. Seat
Defines the seats available for each aircraft.
Normally this is created only once per aircraft.
Screens: 
- View Seats
- Enable Seat
- Disable Seat

6. Reservation
Books a seat for a passenger.

Screens
- Create Reservation
- Search Reservation
- Cancel Reservation

7. Check-in
Confirms the passenger arrived for the flight.

Screens
1. Check-in Passenger
2. View Check-in


# Naming standard

| Tipo             | Estándar        | Ejemplo   |
| ---------------- | --------------- | --------- |
| COBOL            | `AR<MOD><FUNC>` | `ARPF01`  |
| BMS Map          | `AR<MOD><FUNC>` | `ARPF01`  |
| Mapset           | `AR<MOD>MS`     | `ARPFMS`  |
| CICS Transaction | `AR<MOD><F>`    | `ARP1`    |
| JCL              | `AR<MOD><FUNC>` | `ARPF01`  |
| Copybook         | `AR<MOD><FUNC>` | `ARPF01C` |


## Modules
PF = Passenger
AP = Airport
AC = Aircraft
FL = Flight
ST = Seat
RS = Reservation
CI = Check-in
BD = Boarding
RP = Reports


## Cobol programs
ARPF00  Passenger menu
ARPF01  Passenger inquiry
ARPF02  Passenger create
ARPF03  Passenger update

ARAP01  Airport inquiry
ARAP02  Airport create
ARAP03  Airport update

ARAC01  Aircraft inquiry
ARAC02  Aircraft create
ARAC03  Aircraft update

ARFL01  Flight inquiry
ARFL02  Flight create
ARFL03  Flight update
ARFL04  Flight cancel

ARST01  Seat inquiry
ARST02  Seat configuration

ARRS01  Reservation inquiry
ARRS02  Reservation create
ARRS03  Reservation cancel

ARCI01  Check-in
ARCI02  Check-in inquiry

ARBD01  Boarding
ARBD02  Boarding inquiry

## BMS Maps
MPPF00
MPPF01
MPPF02
MPPF03

MPAP01
MPAP02
MPAP03

MPFL01
MPFL02
MPFL03
MPFL04

MPRS01
MPRS02
MPRS03

MPCI01
MPCI02

MPBD01
MPBD02

## CICS transactions
ARPF  Passenger
ARAP  Airport
ARAC  Aircraft
ARFL  Flight
ARST  Seat
ARRS  Reservation
ARCI  Check-in
ARBD  Boarding


## TODO — Development Roadmap

### 1. Project Foundation
- [✔️] Define dataset structure
- [✔️] Define naming conventions
- [✔️] Create COBOL copybook library
- [✔️] Create BMS library
- [✔️] Create DBRM library
- [✔️] Create load library
- [✔️] Create standard compile/link JCL
- [✔️] Create standard BMS assembly JCL

### 2. DB2 Database
- [✔️] Create AIRLINE database
- [✔️] Create ARSTS tablespace
- [✔️] Create PASSENGER table
- [✔️] Create AIRPORT table
- [✔️] Create AIRCRAFT table
- [✔️] Create FLIGHT table
- [✔️] Create SEAT table
- [✔️] Create RESERVATION table
- [✔️] Create required primary/unique indexes
- [✔️] Create foreign keys
- [✔️] Insert initial test data
- [✔️] Verify DB2 catalog definitions

### 3. Common COBOL/CICS Components
- [✔️] Create ARCOMMA copybook
- [✔️] Create ARCONST copybook
- [✔️] Create ARERR copybook
- [✔️] Define standard CICS response/error handling
- [✔️] Define standard pseudo-conversational template
- [✔️] Define standard ENTER/PF3/CLEAR handling

### 4. Main Menu
- [ ] Create main menu BMS
- [ ] Create main menu COBOL program
- [ ] Define main menu CICS transaction
- [ ] Implement pseudo-conversational flow
- [ ] Test ENTER/PF3/CLEAR

### 5. Passenger Management Menu
- [✔️] Create MPPF00 BMS
- [✔️] Create ARPF00 COBOL program
- [✔️] Define PF00 transaction
- [✔️] Implement Passenger menu
- [✔️] Implement navigation to Inquiry
- [✔️] Implement navigation to Create
- [✔️] Implement navigation to Update
- [✔️] Implement PF3 → Main Menu

### 6. Passenger Inquiry
- [✔️] Create MPPF01 BMS
- [✔️] Generate COBOL BMS copybook
- [✔️] Create ARPF01 COBOL program
- [✔️] Define PF01 transaction
- [✔️] Implement pseudo-conversational flow
- [✔️] Receive Passenger ID
- [✔️] Validate Passenger ID
- [✔️] SELECT passenger from DB2
- [✔️] Handle SQLCODE = 0
- [✔️] Handle SQLCODE = +100
- [✔️] Handle DB2 errors
- [✔️] Display passenger information
- [✔️] Implement PF3 → Passenger Menu

### 7. Passenger Create
- [✔️] Create MPPF02 BMS
- [✔️] Generate COBOL BMS copybook
- [✔️] Create ARPF02 COBOL program
- [✔️] Define PF02 transaction
- [✔️] Receive passenger information
- [✔️] Validate input
- [✔️] INSERT passenger into DB2
- [✔️] Handle duplicate email
- [✔️] Handle DB2 errors
- [✔️] COMMIT transaction
- [✔️] Display created Passenger ID
- [✔️] Implement PF3 → Passenger Menu

### 8. Passenger Update
- [✔️] Create MPPF03 BMS
- [✔️] Generate COBOL BMS copybook
- [✔️] Create ARPF03 COBOL program
- [✔️] Define PF03 transaction
- [✔️] Receive Passenger ID
- [✔️] SELECT existing passenger
- [✔️] Display passenger information
- [✔️] Receive modified information
- [✔️] Validate input
- [✔️] UPDATE passenger
- [✔️] Handle passenger not found
- [✔️] Handle duplicate email
- [✔️] Handle DB2 errors
- [✔️] COMMIT transaction
- [✔️] Implement PF3 → Passenger Menu

### 9. Flight Management
- [ ] Create Flight menu
- [ ] Implement Flight inquiry
- [ ] Implement Flight create
- [ ] Implement Flight update
- [ ] Implement Flight search
- [ ] Implement Flight/aircraft relationship
- [ ] Implement Flight/airport relationships

### 10. Aircraft Management
- [ ] Create Aircraft menu
- [ ] Implement Aircraft inquiry
- [ ] Implement Aircraft create
- [ ] Implement Aircraft update

### 11. Seat Management
- [ ] Implement seat configuration
- [ ] Implement seat availability inquiry
- [ ] Implement seat assignment

### 12. Reservation Management
- [ ] Create Reservation menu
- [ ] Search available flights
- [ ] Select flight
- [ ] Select passenger
- [ ] Display available seats
- [ ] Assign seat
- [ ] Create reservation
- [ ] Handle duplicate/occupied seat
- [ ] COMMIT reservation
- [ ] Implement reservation inquiry
- [ ] Implement reservation cancellation

### 13. Check-in
- [ ] Create Check-in screen
- [ ] Search reservation
- [ ] Validate reservation status
- [ ] Validate passenger
- [ ] Validate flight
- [ ] Assign/confirm seat
- [ ] Update reservation status
- [ ] COMMIT check-in

### 14. Boarding
- [ ] Create Boarding screen
- [ ] Search reservation
- [ ] Validate check-in status
- [ ] Validate passenger
- [ ] Update boarding status
- [ ] COMMIT boarding

### 15. Error Handling
- [ ] Standardize CICS RESP/RESP2 handling
- [ ] Standardize SQLCODE handling
- [ ] Handle SQLCODE +100
- [ ] Handle DB2 negative SQLCODEs
- [ ] Handle MAPFAIL
- [ ] Handle invalid PF keys
- [ ] Handle invalid user input
- [ ] Display user-friendly messages

### 16. Transaction Management
- [ ] Verify COMMIT points
- [ ] Implement ROLLBACK where required
- [ ] Test failed transactions
- [ ] Test partial failures
- [ ] Verify database consistency

### 17. Testing
- [ ] Test every BMS screen
- [ ] Test ENTER
- [ ] Test PF3
- [ ] Test CLEAR
- [ ] Test invalid input
- [ ] Test missing records
- [ ] Test duplicate records
- [ ] Test DB2 errors
- [ ] Test transaction rollback
- [ ] Test complete reservation flow

### 18. Final Integration
- [ ] Main Menu → Passenger
- [ ] Main Menu → Flight
- [ ] Main Menu → Aircraft
- [ ] Main Menu → Reservation
- [ ] Reservation → Check-in
- [ ] Check-in → Boarding
- [ ] Verify complete end-to-end flow
- [ ] Clean up JCL
- [ ] Document architecture
- [ ] Document transactions
- [ ] Document datasets
- [ ] Document DB2 schema