#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#include "bluetooth_server.hpp"

BluetoothServer ble_server;

void setup() {
  Serial.begin(115200);
  ble_server.init();
}

void loop() {
  delay(2000);
}
