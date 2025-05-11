#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#include "bluetooth_server.hpp"

class ServerCallbacks : public BLEServerCallbacks {
  void onConnect(BLEServer *pServer) {
    Serial.println("Client connected");
  }

  void onDisconnect(BLEServer *pServer) {
    Serial.println("Client disconnected");
  }
};

BluetoothServer::BluetoothServer() {}

void BluetoothServer::init() {
  BLEDevice::init("ESP32_BLE");

  m_server = BLEDevice::createServer();
  m_server->setCallbacks(new ServerCallbacks());

  m_service = m_server->createService(SERVICE_UUID);

  m_characteristic = m_service->createCharacteristic(
    CHARACTERISTIC_UUID, 
    BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_WRITE);

  m_characteristic->setValue("Hello World says Neil");
  m_service->start();

  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();

  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x06);  // functions that help with iPhone connections issue
  pAdvertising->setMinPreferred(0x12);


  BLEDevice::startAdvertising();

  Serial.println("Started server");
}