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
import com.project.harbinger.multiplayer.GameObjectInformation.ObjectType;
import com.project.harbinger.scene.MultiplayerOptionsScene;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothServer extends BluetoothConnection {

	private BluetoothServerSocket serverSocket;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothSocket socket;
	
	public BluetoothServer(BluetoothAdapter bluetoothAdapter, Engine mEngine) {
		this.bluetoothAdapter = bluetoothAdapter;

		try {
			serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(getName(), UUID.fromString("6D2DF50E-06EF-C21C-7DB0-345099A5F64E"));
		} catch (IOException e) {
			Debug.e(e);
		}
		
		try {
			Debug.e("Czekam");
			((MultiplayerOptionsScene) SceneManager.getInstance().getCurrentScene()).setText("Czekam");
			socket = serverSocket.accept();
		} catch (IOException e) {
			Debug.e(e);
		}
		
		Debug.e("Serwer połączony");
		((MultiplayerOptionsScene) SceneManager.getInstance().getCurrentScene()).setText("Serwer połączony");
		
		ois = null;
		oos = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			ois = new ObjectInputStream(bis);
			BufferedOutputStream obs = new BufferedOutputStream(socket.getOutputStream());
			oos = new ObjectOutputStream(obs);
			oos.flush();
		} catch (Exception e) {
			Debug.e(e);
		}
		
		//SceneManager.getInstance().loadMultiplayerServerGameScene(mEngine, this);
		SceneManager.getInstance().loadMultiplayerGameScene(mEngine, this, false);
	}
	
	/*public void sendGameState(List<GameObject> objects) {
		List<GameObjectInformation> list = new ArrayList<GameObjectInformation>();
		
		for (GameObject gobj : objects) {
			GameObjectInformation next;
			Debug.e(String.valueOf(gobj.getBody().getPosition().x));
			// burdel straszny. ObjectType trzeba będzie wpieprzyć do GameObject, żeby nie używać instanceof
			if (gobj instanceof Bullet) {
				next = new GameObjectInformation(ObjectType.BULLET, gobj.getX(),
						gobj.getY());
			} else if (gobj instanceof Cruiser) {
				next = new GameObjectInformation(ObjectType.CRUISER, gobj.getX(),
						gobj.getY());
			} else if (gobj instanceof HeavyFighter) {
				next = new GameObjectInformation(ObjectType.HEAVY_FIGHTER, gobj.getX(),
						gobj.getY());
			} else if (gobj instanceof LightFighter) {
				next = new GameObjectInformation(ObjectType.LIGHT_FIGHTER, gobj.getX(),
						gobj.getY());
			} else if (gobj instanceof Meteor) {
				next = new GameObjectInformation(ObjectType.METEOR, gobj.getX(),
						gobj.getY());
			} else if (gobj instanceof Missile) {
				next = new GameObjectInformation(ObjectType.MISSILE, gobj.getX(),
						gobj.getY());
			} else if (gobj instanceof Player) {
				next = new GameObjectInformation(ObjectType.PLAYER1, gobj.getX(),
						gobj.getY());
			} else {
				next = null;
			}
			
			list.add(next);
		}
		
		try {
			oos.writeObject(list);
			oos.flush();
			Debug.e("Poszło");
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	*/
	
	public void run() {
		super.run();
	}
}
