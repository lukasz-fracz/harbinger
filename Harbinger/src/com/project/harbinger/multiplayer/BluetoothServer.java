package com.project.harbinger.multiplayer;

import java.io.IOException;
import java.util.UUID;

import org.andengine.util.debug.Debug;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothServer extends Thread {

	private BluetoothServerSocket serverSocket;
	private BluetoothAdapter bluetoothAdapter;
	
	public BluetoothServer(BluetoothAdapter bluetoothAdapter) {
		this.bluetoothAdapter = bluetoothAdapter;

		try {
			serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(getName(), UUID.fromString("6D2DF50E-06EF-C21C-7DB0-345099A5F64E"));
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	
	public void run() {
		BluetoothSocket socket = null;
		try {
			Debug.e("Czekam");
			socket = serverSocket.accept();
		} catch (IOException e) {
			Debug.e(e);
		}
		
		try {
			socket.getOutputStream().write("Hello Wolrd".getBytes());
		} catch (IOException e) {
			Debug.e(e);
		}
	}
}
