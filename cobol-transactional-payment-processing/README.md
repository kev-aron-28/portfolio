# COBOL Transactional Payment Processing

A CICS/DB2 banking lab: accounts, login, deposits, withdrawals, and transfers on a 3270-style flow.

It is a learning system for COBOL, CICS, DB2, and JCL—not a production core. Work is meant for a mainframe or Hercules-style environment.

## What exists

Online programs (pseudo-conversational CICS):

| Program | Role |
| --- | --- |
| `MAINMENU` | Menu (login, new account, exit). TRANSID `BANK` |
| `LOGINM` | Email/password against `USERS`. TRANSID `BLG` |
| `NEWACCM` | Register a user, then start an account. TRANSID `BNA` |
| `STRACCM` | Opening balance on `ACCOUNTS_NEW`. TRANSID `BSA` |
| `PROFM` | Profile and balance. TRANSID `BPA` |
| `DEPOSITM` | Deposit |
| `WITHDRAM` | Withdraw with a funds check. TRANSID `BWA` |
| `TRANSFRM` | Transfer to another account id. TRANSID `BTA` |

BMS maps live in `maps/`. Compile, map assembly, and BIND jobs live in `jcl/`. `sql/main.sql` creates `USERS` and `ACCOUNTS_NEW`.

There is a `TRANSACTIONS` table in SQL; programs do not write audit rows.

## Stack

- Enterprise COBOL
- CICS
- DB2
- BMS
- JCL
