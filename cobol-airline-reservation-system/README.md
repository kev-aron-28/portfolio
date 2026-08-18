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


## TODO — Application Development

### 1. Project Foundation
- [x] Define COBOL program naming standard
- [x] Define BMS map naming standard
- [x] Define CICS transaction naming standard
- [x] Define copybook naming standard
- [x] Create COBOL source library
- [x] Create BMS source library
- [x] Create copybook library
- [x] Create DBRM library
- [x] Create load library

### 2. Common Copybooks
- [x] Create ARCOMMA
- [x] Create ARCONST
- [x] Create ARERR
- [x] Create common message definitions
- [x] Create common CICS response definitions
- [x] Create common DB2 error definitions

### 3. Build Process
- [x] Create BMS assembly JCL
- [x] Create COBOL + DB2 precompile JCL
- [x] Create compile JCL
- [x] Create DB2 BIND JCL
- [x] Create link-edit JCL
- [x] Create complete build JCL
- [x] Test complete build process

### 4. CICS Application Template
- [x] Create standard pseudo-conversational COBOL template
- [x] Implement COMMAREA handling
- [x] Implement SEND MAP
- [x] Implement RECEIVE MAP
- [x] Implement ENTER handling
- [x] Implement PF3 handling
- [x] Implement CLEAR handling
- [x] Implement CICS RESP/RESP2 handling
- [x] Implement DB2 SQLCODE handling
- [x] Test template

### 5. Main Menu
- [ ] Create Main Menu BMS
- [ ] Create Main Menu COBOL program
- [ ] Create Main Menu transaction
- [ ] Implement navigation
- [ ] Test PF3 / ENTER / CLEAR

### 6. Passenger
- [x] Create Passenger Menu BMS
- [x] Create Passenger Menu COBOL program
- [x] Create Passenger Menu transaction

- [x] Create Passenger Inquiry BMS
- [x] Create Passenger Inquiry COBOL program
- [x] Create Passenger Inquiry transaction

- [x] Create Passenger Create BMS
- [x] Create Passenger Create COBOL program
- [x] Create Passenger Create transaction

- [x] Create Passenger Update BMS
- [x] Create Passenger Update COBOL program
- [x] Create Passenger Update transaction

- [x] Test complete Passenger flow

### 7. Airport
- [ ] Create Airport Menu BMS
- [ ] Create Airport Menu COBOL program
- [ ] Create Airport Menu transaction

- [ ] Create Airport Inquiry
- [ ] Create Airport Create
- [ ] Create Airport Update

- [ ] Test complete Airport flow

### 8. Aircraft
- [ ] Create Aircraft Menu BMS
- [ ] Create Aircraft Menu COBOL program
- [ ] Create Aircraft Menu transaction

- [ ] Create Aircraft Inquiry
- [ ] Create Aircraft Create
- [ ] Create Aircraft Update

- [ ] Test complete Aircraft flow

### 9. Flight
- [ ] Create Flight Menu BMS
- [ ] Create Flight Menu COBOL program
- [ ] Create Flight Menu transaction

- [ ] Create Flight Inquiry
- [ ] Create Flight Create
- [ ] Create Flight Update
- [ ] Create Flight Search

- [ ] Test complete Flight flow

### 10. Seat
- [ ] Create Seat Inquiry
- [ ] Create Seat Availability
- [ ] Create Seat Assignment
- [ ] Test Seat flow

### 11. Reservation
- [ ] Create Reservation Menu
- [ ] Create Reservation Search
- [ ] Create Reservation Create
- [ ] Create Reservation Inquiry
- [ ] Create Reservation Cancellation
- [ ] Test Reservation flow

### 12. Check-in
- [ ] Create Check-in BMS
- [ ] Create Check-in COBOL program
- [ ] Create Check-in transaction
- [ ] Search reservation
- [ ] Validate reservation
- [ ] Confirm passenger
- [ ] Confirm seat
- [ ] Update reservation
- [ ] Test Check-in flow

### 13. Boarding
- [ ] Create Boarding BMS
- [ ] Create Boarding COBOL program
- [ ] Create Boarding transaction
- [ ] Validate check-in
- [ ] Confirm passenger
- [ ] Update boarding status
- [ ] Test Boarding flow

### 14. Error Handling
- [ ] Standardize CICS errors
- [ ] Standardize DB2 errors
- [ ] Handle SQLCODE +100
- [ ] Handle negative SQLCODE
- [ ] Handle MAPFAIL
- [ ] Handle invalid input
- [ ] Handle invalid PF keys
- [ ] Display user-friendly messages

### 15. Integration
- [ ] Main Menu → Passenger
- [ ] Main Menu → Airport
- [ ] Main Menu → Aircraft
- [ ] Main Menu → Flight
- [ ] Main Menu → Reservation

- [ ] Passenger → Reservation
- [ ] Flight → Reservation
- [ ] Reservation → Check-in
- [ ] Check-in → Boarding

### 16. Final Testing
- [ ] Test all transactions
- [ ] Test all PF keys
- [ ] Test CLEAR
- [ ] Test invalid input
- [ ] Test DB2 errors
- [ ] Test CICS errors
- [ ] Test pseudo-conversational flow
- [ ] Test COMMAREA
- [ ] Test complete end-to-end reservation flow