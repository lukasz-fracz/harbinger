package com.project.harbinger.multiplayer;

import java.io.IOException;
import java.util.UUID;

import org.andengine.util.debug.Debug;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothClient extends Thread {
	
	private BluetoothDevice device;
	private BluetoothSocket socket;
	
	public BluetoothClient(BluetoothDevice device) {
		try {
			socket = device.createRfcommSocketToServiceRecord(UUID.fromString("6D2DF50E-06EF-C21C-7DB0-345099A5F64E"));
		} catch (IOException e) {
			Debug.e(e);
		}
		
		try {
			socket.connect();
		} catch (IOException e) {
			Debug.e(e);
		}
		
		Debug.e("Połączony");
		
		byte[] buffer = new byte[20];
		try {
			socket.getInputStream().read(buffer);
		} catch (IOException e) {
			Debug.e(e);
		}
		
		Debug.e(String.valueOf(buffer));
	}
}
