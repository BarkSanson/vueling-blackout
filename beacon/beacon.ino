#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#include "bluetooth_server.hpp"

BluetoothServer ble_server;

void setup() {
  Serial.begin(115200);
  //Serial.println("Started server");
  ble_server.init();
}

void loop() {
  // put your main code here, to run repeatedly:
  //Serial.println("Started server");
  delay(2000);
}
