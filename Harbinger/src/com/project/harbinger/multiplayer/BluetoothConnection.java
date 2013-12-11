package com.project.harbinger.multiplayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.andengine.util.debug.Debug;

import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.scene.MultiplayerGameScene;

public abstract class BluetoothConnection extends Thread {
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	public void run() {
		while (true) {
			String message = "";
			try {
				message = (String) ois.readObject();
			} catch (Exception e) {
				Debug.e(e);
			}
			int dot = message.indexOf("-");
			float x = Float.valueOf(message.substring(0, dot));
			float y = Float.valueOf(message.substring(dot + 1));
			((MultiplayerGameScene) SceneManager.getInstance().getCurrentScene()).setPlayerVelocity(x, y);
		}
	}

	public void sendMessage(String message) {
		Debug.e("Wysyłam");
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			Debug.e(e);
		}
	}
}
