#include <SPI.h>
#include <Adafruit_GFX.h>
#include <Adafruit_GC9A01A.h>
#include <mcp2515.h>

// ---------- TFT ----------
#define TFT_CS   27
#define TFT_DC   26
#define TFT_MOSI 13
#define TFT_SCLK 14

// ---------- CAN ----------
#define CAN_CS   5
#define CAN_MOSI 23
#define CAN_MISO 19
#define CAN_SCK  18

#define POT_PIN 34

// SPI buses
SPIClass hspi(HSPI);
SPIClass vspi(VSPI);

// pantalla usando HSPI
Adafruit_GC9A01A tft(&hspi, TFT_DC, TFT_CS);

// CAN usando VSPI
MCP2515 mcp2515(CAN_CS);

struct can_frame canMsg;

const int centerX = 120;
const int centerY = 120;
const int radius = 115;

float velocidad = 0;
float velocidadAnterior = 0;

void drawGauge()
{
    tft.fillScreen(GC9A01A_BLACK);

    for(int a=-120; a<60; a++)
    {
        float rad = a * PI / 180;

        int x1 = centerX + cos(rad)*(radius-6);
        int y1 = centerY + sin(rad)*(radius-6);

        int x2 = centerX + cos(rad)*(radius);
        int y2 = centerY + sin(rad)*(radius);

        tft.drawLine(x1,y1,x2,y2,GC9A01A_CYAN);
    }

    for(int a=60; a<=120; a++)
    {
        float rad = a * PI / 180;

        int x1 = centerX + cos(rad)*(radius-6);
        int y1 = centerY + sin(rad)*(radius-6);

        int x2 = centerX + cos(rad)*(radius);
        int y2 = centerY + sin(rad)*(radius);

        tft.drawLine(x1,y1,x2,y2,GC9A01A_RED);
    }

    for (int i = 0; i <= 150; i++)
    {
        float angle = map(i, 0, 150, -120, 120) * PI / 180;

        int r1 = radius - 8;
        int r2 = radius;

        if (i % 10 == 0)
            r1 = radius - 18;

        if (i % 5 == 0)
            r1 = radius - 12;

        int x1 = centerX + cos(angle) * r1;
        int y1 = centerY + sin(angle) * r1;

        int x2 = centerX + cos(angle) * r2;
        int y2 = centerY + sin(angle) * r2;

        tft.drawLine(x1, y1, x2, y2, GC9A01A_WHITE);
    }

    tft.setTextColor(GC9A01A_WHITE);
    tft.setTextSize(2);

    for (int i = 0; i <= 150; i += 30)
    {
        float angle = map(i, 0, 150, -120, 120) * PI / 180;

        int r = radius - 40;

        int x = centerX + cos(angle) * r;
        int y = centerY + sin(angle) * r;

        tft.setCursor(x - 10, y - 10);
        tft.print(i);
    }

    tft.setCursor(95, 200);
    tft.print("km/h");
}

void drawNeedle(float speed, uint16_t color)
{
    float angle = map(speed, 0, 150, -120, 120) * PI / 180;

    int x = centerX + cos(angle) * (radius - 35);
    int y = centerY + sin(angle) * (radius - 35);

    tft.drawLine(centerX, centerY, x, y, color);

    tft.fillCircle(centerX, centerY, 6, GC9A01A_WHITE);
    tft.fillCircle(centerX, centerY, 3, GC9A01A_BLACK);
}

void drawSpeedNumber(int speed)
{
    tft.fillRect(70, 85, 100, 50, GC9A01A_BLACK);

    tft.setTextColor(GC9A01A_CYAN);
    tft.setTextSize(4);

    tft.setCursor(70, 90);
    tft.print(speed);
}

void setup()
{
  Serial.begin(115200);

  analogReadResolution(12);

  // -------- iniciar HSPI (pantalla) --------
  hspi.begin(TFT_SCLK, -1, TFT_MOSI, TFT_CS);

  tft.begin();
  drawGauge();

  // -------- iniciar VSPI (CAN) --------
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
}

void loop()
{
  int potValue = analogRead(POT_PIN);

  velocidad = map(potValue,0,4095,0,150);

  drawNeedle(velocidadAnterior,GC9A01A_BLACK);
  drawNeedle(velocidad,GC9A01A_RED);

  drawSpeedNumber(velocidad);

  velocidadAnterior = velocidad;

  int v = (int)velocidad;

  canMsg.can_id  = 0x100;
  canMsg.can_dlc = 2;

  canMsg.data[0] = (v >> 8) & 0xFF;
  canMsg.data[1] = v & 0xFF;

  mcp2515.sendMessage(&canMsg);

  delay(20);
}