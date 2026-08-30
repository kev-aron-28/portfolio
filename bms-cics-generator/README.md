# BMS Generator

Small utility for generating IBM CICS BMS maps from a simplified screen definition.

The main purpose of this project is to make the creation of BMS maps easier while enforcing the strict formatting and column-position rules required by traditional mainframe source code.

The generator works with a logical **24x80 screen canvas** and converts the screen definition into valid BMS source code.

---

## Goals

The generator should:

* Generate valid BMS source code.
* Work with the standard 3270 `24x80` screen size.
* Automatically position fields and static text.
* Respect strict source-code columns.
* Automatically split long `INITIAL` values.
* Automatically split static screen content when it exceeds the available BMS line length.
* Prevent fields from exceeding the `24x80` screen.
* Keep the generated source consistent and readable.
* Use commas consistently between BMS parameters.

---

# BMS Source Formatting Rules

The generated BMS source must follow strict column positioning.

## 1. BMS statement position

A BMS definition such as:

```text
        DFHMDF POS=(2,1),                                              X
```

must start at **column 9**.

Therefore:

```text
123456789
        D
```

`DFHMDF` occupies columns `9-14`.

---

## 2. Parameter position

Parameters must start at **column 11** when they continue on subsequent lines.

Example:

```text
        DFHMDF POS=(2,1),                                              X
               LENGTH=26,                                              X
               ATTRB=PROT,                                             X
               INITIAL='AIRLINE RESERVATION SYSTEM'
```

The parameter lines are aligned consistently.

The generator must preserve this indentation automatically.

---

# Continuation Rule

Column `72` is reserved for the BMS continuation character:

```text
X
```

Therefore, when a logical BMS statement continues onto another physical source line:

```text
        DFHMDF POS=(2,1),                                              X
               LENGTH=26,                                              X
               ATTRB=PROT,                                             X
               INITIAL='AIRLINE RESERVATION SYSTEM'
```

the `X` must be located at **column 72**.

The generator must never place content beyond column 72 on a continued line.

---

# Maximum Content Area

Because column `72` is reserved for the continuation character, the usable source area is:

```text
Column 1 ─────────────────────────────── Column 71
                                           Column 72 = X
```

For continued BMS lines, generated content must end no later than column `70`, leaving the required formatting space before the `X`.

For example:

```text
        DFHMDF POS=(2,1),                                              X
```

The generator must calculate the exact available space based on the starting column of the generated content.

---

# INITIAL Values

`INITIAL` values are enclosed in single quotes:

```text
INITIAL='AIRLINE RESERVATION SYSTEM'
```

The quotation marks are part of the generated source and therefore must be considered when calculating the available space.

For example:

```text
INITIAL='TEXT'
```

has a total source length of:

```text
INITIAL= + ' + TEXT + '
```

The generator must calculate the complete length rather than considering only the text inside the quotes.

If an `INITIAL` value cannot fit on the current physical source line, it must be split according to the generator's continuation rules.

---

# BMS Parameters

BMS parameters must be separated using commas.

Example:

```text
DFHMDF POS=(2,1),LENGTH=26,ATTRB=PROT,INITIAL='HELLO'
```

When formatted into multiple source lines:

```text
        DFHMDF POS=(2,1),                                              X
               LENGTH=26,                                              X
               ATTRB=PROT,                                             X
               INITIAL='HELLO'
```

The generator should consistently use:

```text
,
```

between parameters.

The generator should not generate unnecessary alternative separators or formatting styles.

---

# Screen Canvas

The logical screen is based on a standard 3270 screen:

```text
SIZE=(24,80)
```

The generated map should therefore use:

```text
SCRN1   DFHMDI SIZE=(24,80),LINE=1,COLUMN=1
```

The coordinate system is:

```text
        Columns
        1                   80
        ├────────────────────┤
Row 1   │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
        │                    │
Row 24  │                    │
        └────────────────────┘
```

Every generated screen element must fit inside this `24x80` logical canvas.

---

# Screen Coordinates

Each element has a logical position:

```text
ROW
COLUMN
```

For example:

```text
POS=(2,1)
```

means:

```text
Row    = 2
Column = 1
```

An element with:

```text
POS=(2,1)
LENGTH=26
```

occupies:

```text
Columns 1 - 26
```

The generator must validate that:

```text
COLUMN + LENGTH - 1 <= 80
```

Otherwise, the element does not fit on the screen.

---

# Static Text

Static screen content should be treated as an element placed on the logical `24x80` canvas.

For example, if the user wants:

```text
................................................
```

starting at:

```text
ROW=2
COLUMN=1
```

the generator must determine how much of the text can fit into the available screen area.

The physical BMS source representation is independent from the logical screen representation.

This distinction is important:

```text
Logical screen
       ↓
24 x 80 canvas
       ↓
BMS elements
       ↓
Physical source lines
       ↓
Columns 1-72
```

---

# Splitting Screen Content

A logical element may need to be split into multiple BMS definitions when it cannot be represented as a single BMS statement or when the element exceeds the available screen area.

For example, a logical line:

```text
................................................................................
```

contains 80 characters.

If it starts at:

```text
COLUMN=1
```

it occupies:

```text
COLUMN 1 ─────────────── COLUMN 80
```

If the element were longer than the available screen width, it would need to be divided into multiple screen elements.

The generator must therefore distinguish between:

### Logical length

The length of the actual screen content.

### Source length

The number of characters required to represent that content in the BMS source.

These are not necessarily the same.

---

# Example Map Header

A generated map may start with:

```text
        MPFT00  DFHMSD TYPE=MAP,STORAGE=AUTO,LANG=COBOL,TIOAPFX=YES,   X
               TERM=3270,CTRL=FREEKB,MODE=INOUT
SCRN1   DFHMDI SIZE=(24,80),LINE=1,COLUMN=1
```

The generator must preserve the required positions of:

```text
MPFT00
DFHMSD
SCRN1
DFHMDI
```

and the corresponding parameters.

---

# Example Generated Field

Given a logical field:

```text
FIELD
    position = (2,1)
    length = 26
    attribute = PROT
    initial = "AIRLINE RESERVATION SYSTEM"
```

the generated BMS should be:

```text
        DFHMDF POS=(2,1),                                              X
               LENGTH=26,                                              X
               ATTRB=PROT,                                             X
               INITIAL='AIRLINE RESERVATION SYSTEM'
```

The generator is responsible for calculating the formatting and continuation positions automatically.

---

# Architecture

The project should be divided into several logical stages.

```text
Input
  │
  ▼
Parser
  │
  ▼
Screen Model
  │
  ▼
Validation
  │
  ▼
BMS Generator
  │
  ▼
Source Formatter
  │
  ▼
.bms
```

## 1. Input

A simple representation of the desired screen.

Example:

```text
SCREEN Flight Search

FIELD TITLE
    ROW=2
    COLUMN=1
    TYPE=TEXT
    VALUE="AIRLINE RESERVATION SYSTEM"

FIELD FLIGHT
    ROW=5
    COLUMN=10
    LENGTH=8
    TYPE=INPUT
```

---

## 2. Screen Model

The input is converted into an internal representation.

Example:

```text
Screen
 ├── width: 80
 ├── height: 24
 │
 └── elements
      ├── TextElement
      ├── InputField
      ├── OutputField
      └── ...
```

---

## 3. Validation

Before generating BMS, the generator validates:

* Row is between `1` and `24`.
* Column is between `1` and `80`.
* Element does not exceed the screen width.
* Element does not exceed the screen height.
* Element names are valid.
* Element names are unique.
* BMS parameters are valid.
* Generated source lines respect the column restrictions.

---

## 4. BMS Generator

The generator converts the screen model into BMS definitions:

```text
DFHMSD
DFHMDI
DFHMDF
DFHMSD TYPE=FINAL
```

depending on the final implementation.

---

## 5. Source Formatter

The formatter is responsible for the physical source-code rules.

This includes:

* Column 9 for `DFHMDF`.
* Column 11 for continued parameters.
* Column 72 for continuation `X`.
* Maximum content width.
* Commas between parameters.
* Correct indentation.
* Splitting long parameter values.
* Preserving quoted `INITIAL` values.

This component should be independent from the screen logic.

---

# Important Design Principle

The project must distinguish between:

```text
SCREEN SPACE
```

and:

```text
SOURCE CODE SPACE
```

The screen is:

```text
24 x 80
```

while the BMS source has its own physical formatting restrictions.

For example:

```text
Screen:

COLUMN 1                              COLUMN 80
│                                        │
▼                                        ▼
................................................
```

does **not** mean that the generated source can simply contain 80 characters on one line.

The generator must first create the logical BMS element and then format that element according to the source-code column rules.

---

# Initial Scope

The first version of the generator should focus only on:

* `DFHMSD`
* `DFHMDI`
* `DFHMDF`
* `POS`
* `LENGTH`
* `ATTRB`
* `INITIAL`
* `24x80` screen validation
* Source column validation
* Continuation using `X`
* Automatic source-line splitting
* Comma-separated parameters

More BMS attributes and advanced features can be added later.

---

# Future Features

Possible future functionality:

* `PICIN`
* `PICOUT`
* `COLOR`
* `HILIGHT`
* `JUSTIFY`
* `FSET`
* `ASKIP`
* `PROT`
* `UNPROT`
* `NUM`
* `BRT`
* `DRK`
* `IC`
* Automatic COBOL copybook generation.
* Preview of the `24x80` screen.
* Collision detection.
* Import existing BMS.
* Export `.bms` and `.cpy`.
* CLI interface.
* Web-based screen designer.

---

# Project Philosophy

The generator should not attempt to hide the limitations of BMS.

Instead, it should make those limitations explicit and automatically handle the tedious parts:

```text
Developer
    │
    │ Defines screen
    ▼
BMS Generator
    │
    ├── Validates 24x80
    ├── Calculates positions
    ├── Splits content
    ├── Formats parameters
    ├── Adds continuation X
    └── Generates source
            │
            ▼
        Valid BMS
```

The goal is to allow the developer to think primarily in terms of the **screen**, while the generator handles the strict formatting requirements of traditional BMS source code.
