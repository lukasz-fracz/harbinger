package com.project.harbinger.multiplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.andengine.engine.Engine;
import org.andengine.util.debug.Debug;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;
import com.project.harbinger.scene.MultiplayerClientGameScene;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothClient extends Thread {
	
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private Engine mEngine;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public BluetoothClient(BluetoothDevice device, Engine mEngine) {
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
			Debug.e(e);
		}
		
		Debug.e("Klient Połączony");
		
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
		
		SceneManager.getInstance().loadMultiplayerClientGameScene(mEngine);
	}
	
	public void run() {
		
		while (SceneManager.getInstance().getCurrentScene().getSceneType() != SceneType.SCENE_MULTIPLAYER_CILENT_GAME) {
			try {
				wait(1000);
			} catch (Exception e) {}
		}
		
		while (true) {
			List<GameObjectInformation> list = null;
			
			try {
				list = (ArrayList<GameObjectInformation>) ois.readObject();
			} catch (Exception e) {
				Debug.e(e);
			}
			
			((MultiplayerClientGameScene) SceneManager.getInstance().getCurrentScene()).renderObjects(list);
		}
	}
}
