package com.project.harbinger.multiplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.andengine.util.debug.Debug;

import com.project.harbinger.multiplayer.GameObjectInformation.ObjectType;

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
		
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			ois = new ObjectInputStream(bis);
			BufferedOutputStream obs = new BufferedOutputStream(socket.getOutputStream());
			oos = new ObjectOutputStream(obs);
			oos.flush();
		} catch (Exception e) {
			Debug.e(e);
		}
		
		int i = 0;
		
		while (true) {
			List<GameObjectInformation> list = new ArrayList<GameObjectInformation>();
			list.add(new GameObjectInformation(ObjectType.CRUISER, i, 500));
			
			try {
				oos.writeObject(list);
				oos.flush();
			} catch (IOException e) {
				Debug.e(e);
			}
			
			i++;
			try {
				wait(100);
			} catch (Exception e) {}
		}
	}
}
