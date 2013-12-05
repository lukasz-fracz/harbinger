package com.project.harbinger.multiplayer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
		
		Debug.e("Serwer połączony");
		
		try {
			socket.getOutputStream().write("Hello Wolrd".getBytes());
		} catch (IOException e) {
			Debug.e(e);
		}
		byte[] buffer = new byte[20];
		try {
			socket.getInputStream().read(buffer);
		} catch (IOException e) {
			Debug.e(e);
		}
		
		Debug.e(new String(buffer));
		Debug.e("Czekabssdsdfsdfsdfdsffm");
		
		ObjectInputStream ois = null;
		try {
			Debug.e("Tu");
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			Debug.e("Tam");
			ois = new ObjectInputStream(bis);
			Debug.e("Czekabdfm");
			String my = (String) ois.readObject();
			Debug.e(my);
		} catch (Exception e) {
			Debug.e(e);
		}
	}
}
