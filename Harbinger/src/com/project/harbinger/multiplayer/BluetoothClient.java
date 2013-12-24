package com.project.harbinger.multiplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.andengine.engine.Engine;
import org.andengine.util.debug.Debug;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.scene.MultiplayerOptionsScene;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothClient extends BluetoothConnection {
	
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private Engine mEngine;
	
	
	public BluetoothClient(BluetoothDevice device, Engine mEngine) throws IOException {
		this.device = device;
		this.mEngine = mEngine;
		
		try {
			socket = device.createRfcommSocketToServiceRecord(UUID.fromString("6D2DF50E-06EF-C21C-7DB0-345099A5F64E"));
		} catch (IOException e) {
			Debug.e(e);
		}
		
		try {
			socket.connect();
		} catch (IOException e) {
			throw e;
		}
		
		Debug.e("Klient Połączony");
		((MultiplayerOptionsScene) SceneManager.getInstance().getCurrentScene()).setStatus(MultiplayerOptionsScene.FOUND);
		
		oos = null;
		ois = null;
		
		try {
			BufferedOutputStream obs = new BufferedOutputStream(socket.getOutputStream());
			oos = new ObjectOutputStream(obs);
			oos.flush();
			BufferedInputStream ibs = new BufferedInputStream(socket.getInputStream());
			ois = new ObjectInputStream(ibs);
		} catch (Exception e) {
			Debug.e(e);
		}
		
		//SceneManager.getInstance().loadMultiplayerClientGameScene(mEngine);
		SceneManager.getInstance().loadMultiplayerGameScene(mEngine, this, true);
	}
	
	public void run() {
		super.run();
	}
}
