-- ============================================================
-- Airline Reservation System
-- DB2 Database Creation
--
-- Purpose:
-- Creates the database structures required for the
-- Airline Reservation System project.
--
-- Technologies:
-- - DB2 for z/OS
-- - COBOL
-- - CICS
-- ============================================================


-- ============================================================
-- 1. Storage Group
--
-- Defines where DB2 will physically store the data.
--
-- SBSYS1 is the DASD volume available in Hercules.
-- DSNCAT is the catalog used by DB2.
-- ============================================================

CREATE STOGROUP AIRSTOG
VOLUMES(SBSYS1)
VCAT DSNCAT;



-- ============================================================
-- 2. Database
--
-- Logical container for all airline application objects.
-- ============================================================

CREATE DATABASE AIRLINE
STOGROUP AIRSTOG
BUFFERPOOL BP0;



-- ============================================================
-- 3. Tablespace
--
-- Physical storage area for application tables.
--
-- LOCKSIZE ROW:
--   Enables row-level locking.
--
-- LOCKMAX SYSTEM:
--   DB2 controls lock escalation.
--
-- CLOSE YES:
--   Closes datasets when not being accessed.
-- ============================================================

CREATE TABLESPACE ARSTS
IN AIRLINE
USING STOGROUP AIRSTOG
PRIQTY 500
SECQTY 100
BUFFERPOOL BP0
LOCKSIZE ROW
LOCKMAX SYSTEM
CLOSE YES;



-- ============================================================
-- 4. AIRPORT
--
-- Stores airport information.
--
-- Example:
-- MEX -> Mexico City Airport
-- JFK -> John F. Kennedy Airport
-- ============================================================

CREATE TABLE AIRPORT
(
    AIRPORT_ID INTEGER GENERATED ALWAYS AS IDENTITY,
    CODE CHAR(3) NOT NULL,
    NAME VARCHAR(100) NOT NULL,
    CITY VARCHAR(50) NOT NULL,
    COUNTRY VARCHAR(50) NOT NULL,
    CONSTRAINT PK_AIRPORT
    PRIMARY KEY(AIRPORT_ID),
    CONSTRAINT UQ_AIRPORT_CODE
    UNIQUE(CODE)

)
IN AIRLINE.ARSTS;

CREATE UNIQUE INDEX IDX_AIRPORT_ID
ON IBMUSER.AIRPORT
(
    AIRPORT_ID
);

-- ============================================================
-- 5. AIRCRAFT
--
-- Stores aircraft information.
--
-- Example:
-- Boeing 737
-- Airbus A320
-- ============================================================

CREATE TABLE AIRCRAFT
(
    AIRCRAFT_ID INTEGER GENERATED ALWAYS AS IDENTITY,
    MODEL VARCHAR(50) NOT NULL,
    CAPACITY INTEGER NOT NULL,
    STATUS CHAR(1)
    DEFAULT 'A',
    CONSTRAINT PK_AIRCRAFT
    PRIMARY KEY(AIRCRAFT_ID)
)
IN AIRLINE.ARSTS;

CREATE UNIQUE INDEX IDX_AIRCRAFT_ID
ON IBMUSER.AIRCRAFT
(
    AIRCRAFT_ID
);

-- ============================================================
-- 6. PASSENGER
--
-- Stores customer/passenger information.
-- ============================================================

CREATE TABLE PASSENGER
(
    PASSENGER_ID INTEGER GENERATED ALWAYS AS IDENTITY,
    FIRST_NAME VARCHAR(50) NOT NULL,
    LAST_NAME VARCHAR(50) NOT NULL,
    EMAIL VARCHAR(100) NOT NULL,
    STATUS CHAR(1)
    DEFAULT 'A',
    CONSTRAINT PK_PASSENGER
    PRIMARY KEY(PASSENGER_ID),
    CONSTRAINT UQ_PASSENGER_EMAIL
    UNIQUE(EMAIL)
) IN AIRLINE.ARSTS;


CREATE UNIQUE INDEX IDX_PASSENGER_ID
ON IBMUSER.PASSENGER
(
    PASSENGER_ID
);


-- ============================================================
-- 7. FLIGHT
--
-- Represents scheduled flights.
--
-- A flight belongs to:
-- - One aircraft
-- - One departure airport
-- - One arrival airport
-- ============================================================

CREATE TABLE FLIGHT
(
    FLIGHT_ID INTEGER GENERATED ALWAYS AS IDENTITY,
    FLIGHT_NUMBER CHAR(6) NOT NULL,
    AIRCRAFT_ID INTEGER NOT NULL,
    ORIGIN_ID INTEGER NOT NULL,
    DESTINATION_ID INTEGER NOT NULL,
    DEPARTURE_TIME TIMESTAMP NOT NULL,
    ARRIVAL_TIME TIMESTAMP NOT NULL,
    STATUS CHAR(1)
    DEFAULT 'S',
    CONSTRAINT PK_FLIGHT
    PRIMARY KEY(FLIGHT_ID)
) IN AIRLINE.ARSTS;

CREATE UNIQUE INDEX IDX_FLIGHT_ID
ON IBMUSER.FLIGHT
(
    FLIGHT_ID
);



-- ============================================================
-- 8. SEAT
--
-- Stores seats available per aircraft.
--
-- Example:
-- 10A
-- 10B
-- ============================================================

CREATE TABLE SEAT
(
    SEAT_ID INTEGER GENERATED ALWAYS AS IDENTITY,
    AIRCRAFT_ID INTEGER NOT NULL,
    SEAT_NUMBER CHAR(3) NOT NULL,
    STATUS CHAR(1)
    DEFAULT 'A',
    CONSTRAINT PK_SEAT
    PRIMARY KEY(SEAT_ID)
)
IN AIRLINE.ARSTS;

CREATE UNIQUE INDEX IDX_SEAT_ID
ON IBMUSER.SEAT
(
    SEAT_ID
);

CREATE UNIQUE INDEX IBMUSER.IDX_PASSENGER_EMAIL
ON IBMUSER.PASSENGER (EMAIL);


-- ============================================================
-- 9. RESERVATION
--
-- Stores passenger flight reservations.
-- ============================================================

CREATE TABLE RESERVATION
(
    RESERVATION_ID INTEGER GENERATED ALWAYS AS IDENTITY,
    PASSENGER_ID INTEGER NOT NULL,
    FLIGHT_ID INTEGER NOT NULL,
    SEAT_ID INTEGER,
    CREATED_AT TIMESTAMP
    DEFAULT CURRENT TIMESTAMP,
    STATUS CHAR(1)
    DEFAULT 'C',
    CONSTRAINT PK_RESERVATION
    PRIMARY KEY(RESERVATION_ID)
)
IN AIRLINE.ARSTS;

CREATE UNIQUE INDEX IDX_RESERVATION_ID
ON IBMUSER.RESERVATION
(
    RESERVATION_ID
);


-- ============================================================
-- 10. CHECKIN
--
-- Stores passenger check-in information.
-- ============================================================

CREATE TABLE CHECKIN
(
    CHECKIN_ID INTEGER GENERATED ALWAYS AS IDENTITY,
    RESERVATION_ID INTEGER NOT NULL,
    CHECKIN_TIME TIMESTAMP
    DEFAULT CURRENT TIMESTAMP,
    BAGGAGE_COUNT INTEGER
    DEFAULT 0,
    CONSTRAINT PK_CHECKIN
    PRIMARY KEY(CHECKIN_ID)
) IN AIRLINE.ARSTS;

CREATE UNIQUE INDEX IDX_CHECKIN_ID
ON IBMUSER.CHECKIN
(
    CHECKIN_ID
);


-- ============================================================
-- Foreign Keys
-- ============================================================
ALTER TABLE IBMUSER.FLIGHT
ADD CONSTRAINT FK_FLIGHT_AIRCRAFT
FOREIGN KEY(AIRCRAFT_ID)
REFERENCES IBMUSER.AIRCRAFT(AIRCRAFT_ID);


ALTER TABLE IBMUSER.FLIGHT
ADD CONSTRAINT FK_FLIGHT_ORIGIN
FOREIGN KEY(ORIGIN_ID)
REFERENCES IBMUSER.AIRPORT(AIRPORT_ID);


ALTER TABLE IBMUSER.FLIGHT
ADD CONSTRAINT FK_FLIGHT_DESTINATION
FOREIGN KEY(DESTINATION_ID)
REFERENCES IBMUSER.AIRPORT(AIRPORT_ID);


ALTER TABLE IBMUSER.SEAT
ADD CONSTRAINT FK_SEAT_AIRCRAFT
FOREIGN KEY(AIRCRAFT_ID)
REFERENCES IBMUSER.AIRCRAFT(AIRCRAFT_ID);


ALTER TABLE IBMUSER.RESERVATION
ADD CONSTRAINT FK_RES_PASSENGER
FOREIGN KEY(PASSENGER_ID)
REFERENCES IBMUSER.PASSENGER(PASSENGER_ID);


ALTER TABLE IBMUSER.RESERVATION
ADD CONSTRAINT FK_RES_FLIGHT
FOREIGN KEY(FLIGHT_ID)
REFERENCES IBMUSER.FLIGHT(FLIGHT_ID);


ALTER TABLE IBMUSER.RESERVATION
ADD CONSTRAINT FK_RES_SEAT
FOREIGN KEY(SEAT_ID)
REFERENCES IBMUSER.SEAT(SEAT_ID);


ALTER TABLE IBMUSER.CHECKIN
ADD CONSTRAINT FK_CHECKIN_RESERVATION
FOREIGN KEY(RESERVATION_ID)
REFERENCES IBMUSER.RESERVATION(RESERVATION_ID);

-- To get timestamp
SELECT CURRENT TIMESTAMP 
FROM SYSIBM.SYSDUMMY1    