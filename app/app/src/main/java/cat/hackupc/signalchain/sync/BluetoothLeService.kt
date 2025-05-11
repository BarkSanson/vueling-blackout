package cat.hackupc.signalchain.sync

import android.Manifest
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.UUID

class BluetoothLeService(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter
) {
    private val bluetoothScanner = bluetoothAdapter.bluetoothLeScanner
    private var gatt: BluetoothGatt? = null

    private val TARGET_DEVICE_NAME = "ESP32_BLE"

    private var scanning = false;
    private var isConnected = false;

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(allOf = [
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN ])
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                val name = it.device.name

                if (name == TARGET_DEVICE_NAME && !isConnected) {
                    stopScan()
                    gatt = it.device.connectGatt(context, false, gattCallback)
                }
            }
        }
    }

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED && gatt != null) {
                Log.d("BLE", "Connected to ${gatt.device.name}")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BLE", "Disconnected from ${gatt?.device?.address}")
                this@BluetoothLeService.gatt = null
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.d("BLE", "Services discovered with status: $status")

            if (status != BluetoothGatt.GATT_SUCCESS || gatt == null) return

            val service = gatt.getService(FLIGHTS_SERVICE_UUID)
            val characteristic = service.getCharacteristic(FLIGHTS_CHARACTERISTIC_UUID) ?: return

            gatt.readCharacteristic(characteristic)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            val stringValue = value.toString(Charsets.UTF_8)

            Log.d("BLE", "Valor leído: $stringValue")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    public fun startScan() {
        if (!scanning) {
            bluetoothScanner.startScan(leScanCallback)
            scanning = true;
            Log.d("BLE", "Starting scan")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun stopScan() {
        if (scanning) {
            bluetoothScanner.stopScan(leScanCallback)
            scanning = false;
        }
    }

    companion object {
        private val FLIGHTS_SERVICE_UUID = UUID.fromString("0d6aad73-55a0-45a1-a77c-149c605408f2")
        private val FLIGHTS_CHARACTERISTIC_UUID = UUID.fromString("f989a02f-539a-4697-8664-5996a3ba8f2a")
    }

}
