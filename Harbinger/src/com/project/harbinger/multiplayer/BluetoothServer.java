package com.project.harbinger.multiplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.andengine.engine.Engine;
import org.andengine.util.debug.Debug;

import com.project.harbinger.gameObject.*;
import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.scene.MultiplayerOptionsScene;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothServer extends BluetoothConnection {

	private BluetoothServerSocket serverSocket;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket socket;
	
	public BluetoothServer(BluetoothAdapter bluetoothAdapter, Engine mEngine) throws IOException {
		this.bluetoothAdapter = bluetoothAdapter;

		serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(getName(), UUID.fromString("6D2DF50E-06EF-C21C-7DB0-345099A5F64E"));
		
		Debug.e("Czekam");
		((MultiplayerOptionsScene) SceneManager.getInstance().getCurrentScene()).setStatus(MultiplayerOptionsScene.WAIT);
		socket = serverSocket.accept();
		
		Debug.e("Serwer połączony");
		((MultiplayerOptionsScene) SceneManager.getInstance().getCurrentScene()).setStatus(MultiplayerOptionsScene.FOUND);
		
		BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
		ois = new ObjectInputStream(bis);
		BufferedOutputStream obs = new BufferedOutputStream(socket.getOutputStream());
		oos = new ObjectOutputStream(obs);
		oos.flush();
		
		//SceneManager.getInstance().loadMultiplayerServerGameScene(mEngine, this);
		SceneManager.getInstance().loadMultiplayerGameScene(mEngine, this, false);
	}
	
	public void run() {
		super.run();
	}
}
