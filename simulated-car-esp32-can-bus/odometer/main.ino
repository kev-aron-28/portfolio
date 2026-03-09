#include <SPI.h>
#include <Adafruit_GFX.h>
#include <Adafruit_GC9A01A.h>
#include <mcp2515.h>

// ---------- TFT ----------
#define TFT_DC   26
#define TFT_CS   27
#define TFT_MOSI 13
#define TFT_SCLK 14

// ---------- CAN ----------
#define CAN_CS   5
#define CAN_MOSI 23
#define CAN_MISO 19
#define CAN_SCK  18

#define SPEED_CAN_ID 0x100

// SPI buses
SPIClass hspi(HSPI);
SPIClass vspi(VSPI);

// pantalla usando HSPI
Adafruit_GC9A01A tft(&hspi, TFT_DC, TFT_CS);

// CAN
MCP2515 mcp2515(CAN_CS);
struct can_frame canMsg;

// odómetro
float kilometros = 0;
float kilometrosPrev = -1;
int velocidad = 0;

unsigned long lastTime = 0;

// ---------- DIBUJAR TABLERO ----------
void drawLayout()
{
    tft.fillScreen(GC9A01A_BLACK);

    for(int a=200; a<340; a++)
    {
        float rad = a * PI / 180;

        int cx = 120;
        int cy = 120;
        int r = 110;

        int x1 = cx + cos(rad)*(r-5);
        int y1 = cy + sin(rad)*(r-5);

        int x2 = cx + cos(rad)*(r);
        int y2 = cy + sin(rad)*(r);

        tft.drawLine(x1,y1,x2,y2,GC9A01A_CYAN);
    }

    tft.setTextColor(GC9A01A_WHITE);
    tft.setTextSize(2);

    tft.setCursor(95,40);
    tft.print("ODO");

    tft.setCursor(100,190);
    tft.print("km");
}

// ---------- DIBUJAR ODOMETRO ----------
void drawKilometraje(float km)
{
    if((int)km == (int)kilometrosPrev) return;

    tft.fillRect(30,80,180,70,GC9A01A_BLACK);

    tft.setTextColor(GC9A01A_BLUE);
    tft.setTextSize(3);

    tft.setCursor(35,90);
    tft.print(km,1);

    kilometrosPrev = km;
}

// ---------- SETUP ----------
void setup()
{
    Serial.begin(115200);

    analogReadResolution(12);

    // ---------- HSPI (pantalla) ----------
    hspi.begin(TFT_SCLK, -1, TFT_MOSI, TFT_CS);

    tft.begin();
    drawLayout();

    // ---------- VSPI (CAN) ----------
    SPI.begin(CAN_SCK, CAN_MISO, CAN_MOSI, CAN_CS);
    Serial.println("Inicializando MCP2515...");

    pinMode(CAN_CS, OUTPUT);
    pinMode(TFT_CS, OUTPUT);

    mcp2515.reset();
    byte status = mcp2515.getStatus();
    Serial.print("Status MCP2515: ");
    Serial.println(status, BIN);
    mcp2515.setBitrate(CAN_500KBPS, MCP_8MHZ);
    mcp2515.setNormalMode();

    Serial.println("MCP2515 listo");

    lastTime = millis();
    
}

// ---------- LOOP ----------
void loop()
{
    // recibir velocidad CAN
    drawKilometraje(kilometros);

    int velocidad = 0;
    if (mcp2515.readMessage(&canMsg) == MCP2515::ERROR_OK) {


        if (canMsg.can_dlc >= 2) {
            velocidad = (canMsg.data[0] << 8) | canMsg.data[1];
        }

        Serial.print("Message received from ID: 0x");
        Serial.print(canMsg.can_id, HEX);

    }

    unsigned long now = millis();
    float deltaTime = (now - lastTime) / 1000.0;
    lastTime = now;

    kilometros += (velocidad * deltaTime) / 3600.0;

    delay(20);
}