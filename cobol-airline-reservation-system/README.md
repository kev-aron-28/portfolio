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