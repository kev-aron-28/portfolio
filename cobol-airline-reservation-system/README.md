# Airline Reservation System

A learning design for a CICS airline application on IBM z/OS (or Hercules).

This repository currently holds the **DB2 schema** (`sql/schema.sql`) and the naming/module plan. COBOL programs, BMS maps, copybooks, and JCL are not in the tree yet.

## Planned modules

Passenger, airport, aircraft, flight, seat, reservation, and check-in. The schema already defines those tables (plus indexes and `FLIGHT_SEQ`). Boarding is named in the design but has no table yet.

Intended stack once application source is added: Enterprise COBOL, CICS, DB2, BMS, JCL.

## Naming (for when programs land)

| Kind | Pattern | Example |
| --- | --- | --- |
| COBOL / BMS / JCL | `AR<MOD><FUNC>` | `ARPF01` |
| Mapset | `AR<MOD>MS` | `ARPFMS` |
| CICS transaction | `AR<MOD><F>` | `ARP1` |
| Copybook | `AR<MOD><FUNC>` | `ARPF01C` |

Modules: PF passenger, AP airport, AC aircraft, FL flight, ST seat, RS reservation, CI check-in.
