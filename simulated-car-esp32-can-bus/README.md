# Simulated Car (ESP32 + CAN)

Two ESP32 sketches that talk over CAN: a speed sender and an odometer receiver.

Hardware on each board: GC9A01A round TFT (240×240) and an MCP2515 CAN controller at 500 kbps.

## `speed/`

Reads a potentiometer on GPIO 34 (mapped to 0–150 km/h), draws a gauge, and sends CAN id `0x100` with a 2-byte big-endian speed about every 20 ms.

## `odometer/`

Listens for `0x100`, treats the payload as km/h, and integrates distance over time on an “ODO” display.

Flash each sketch to its own ESP32. There is no car ECU, OBD-II, or extra protocol beyond that single CAN frame.
