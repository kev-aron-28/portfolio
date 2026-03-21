# Overview

This project implements a transactional payment processing system inspired by a simplified banking core, developed in a mainframe environment using COBOL, CICS, and DB2.
The system manages bank accounts and executes financial operations in real time, ensuring data consistency through transactional control (COMMIT / ROLLBACK) and concurrency handling.

# Objective
To simulate a high-volume enterprise system that processes critical financial operations while applying mainframe best practices:
- Real-time OLTP processing
- Data integrity and consistency
- Error handling and recovery
- Separation of concerns
- Integeration between CICS, COBOL and DB2

# Components:
- CICS: Transaction management and request handling
- COBOL: Business logic implementation
- DB2: Data persistence layer
- JCL: Compilation, binding, and batch execution

# Features
- Account Management
    - Create bank accounts
    - Check account balance
    - Validate account existence
- Financial Operations
    - Deposit
    - Withdraw
    - Transfer between accounts
- Transaction Control
    - COMMIT / ROLLBACK usage
    - Sufficient balance validation
    - Data consistency enforcement
- Audit Logging
    - Record all transactions
    - Timestamp tracking
    - Source and destination accounts

# Screens
There is a maps folder where we define all the mapset definitions 
Main