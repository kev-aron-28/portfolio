# BMS CICS Generator

A browser tool that turns a 24×80 screen layout into IBM CICS BMS source.

You place text and fields on a 3270 canvas; the app emits `DFHMSD` / `DFHMDI` / `DFHMDF` with column-9 statements, column-11 continuations, and `X` in column 72. Open `index.html` locally—there is no backend.

## What it does

- 24×80 screen designer (drag/drop, properties panel)
- Live BMS preview
- Copy or download generated `.bms`
- Bounds checks so fields stay on the screen
- Splits long `INITIAL` values across continued source lines

It generates `POS`, `LENGTH`, `ATTRB`, and `INITIAL`. It does not import existing maps, emit COBOL copybooks, or run as a CLI.

## Source formatting (what the formatter enforces)

```
        DFHMDF POS=(2,1),                                              X
               LENGTH=26,                                              X
               ATTRB=PROT,                                             X
               INITIAL='AIRLINE RESERVATION SYSTEM'
```

- Statement starts at column 9
- Continued parameters start at column 11
- Continuation `X` at column 72
- Logical screen space (24×80) is separate from physical source columns (1–72)

## Files

```
index.html
css/style.css
js/app.js
js/screen.js
js/elements.js
js/validation.js
js/bms-generator.js
js/bms-formatter.js
```
