package com.project.harbinger.multiplayer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.andengine.util.debug.Debug;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothClient extends Thread {
	
	private BluetoothDevice device;
	private BluetoothSocket socket;
	
	public BluetoothClient(BluetoothDevice device) {
		this.device = device;
		
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
		
		Debug.e("Klient Połączony");
		
		byte[] buffer = new byte[20];
		try {
			socket.getInputStream().read(buffer);
		} catch (IOException e) {
			Debug.e(e);
		}
		
		Debug.e(new String(buffer));
		try {
			socket.getOutputStream().write("Dzięki".getBytes());
		} catch (IOException e) {
			Debug.e(e);
		}
		
		ObjectOutputStream oos = null;
		
		try {
			BufferedOutputStream obs = new BufferedOutputStream(socket.getOutputStream());
			Debug.e("Wysyłam");
			oos = new ObjectOutputStream(obs);
			Debug.e("Wysłąłem");
			oos.writeObject("Teraz wysyłam zserializowanego stringa");
			Debug.e("Teraz dopiero wysłąłem");
			oos.flush();
		} catch (Exception e) {
			Debug.e(e);
		}
	}
}
