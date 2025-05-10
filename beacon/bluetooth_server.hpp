#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#define SERVICE_UUID        "0d6aad73-55a0-45a1-a77c-149c605408f2"
#define CHARACTERISTIC_UUID "f989a02f-539a-4697-8664-5996a3ba8f2a"

class BluetoothServer {
private:
  BLEServer *m_server;
  BLEService *m_service;
  BLECharacteristic *m_characteristic;
public:
  BluetoothServer();
  void init();
};