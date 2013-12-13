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
	
	public static final String PAUSE = "pause";
	public static final String MOVE = "move";
	public static final String MISSILE = "missile";
	public static final String DESTROY = "destroy";
	public static final String RESUME = "resume";
	public static final String SCORE = "score";
	public static final String DASH = "-";
	public static final String SEMICOLON = ";";
	
	public void run() {
		while (true) { // oczywiście to true kiedyś zniknie
			String message = "";
			try {
				message = (String) ois.readObject();
			} catch (Exception e) {
				Debug.e(e);
			}
			
			int dash = message.indexOf(DASH);
			String action = message.substring(0, dash);
			Debug.e(message);
			if (action.equals(MOVE)) {
				int semicolon = message.indexOf(SEMICOLON);
				float x = Float.valueOf(message.substring(dash + 1, semicolon));
				float y = Float.valueOf(message.substring(semicolon + 1));
				
				((MultiplayerGameScene) SceneManager.getInstance().getCurrentScene()).movePlayer2(x, y);
			} else if (action.equals(MISSILE)) {
				int semicolon = message.indexOf(SEMICOLON);
				float x = Float.valueOf(message.substring(dash + 1, semicolon));
				float y = Float.valueOf(message.substring(semicolon + 1));
				
				((MultiplayerGameScene) SceneManager.getInstance().getCurrentScene()).addMissile(x, y);
			} else if (action.equals(PAUSE)) {
				((MultiplayerGameScene) SceneManager.getInstance().getCurrentScene()).pauseGame();
			} else if (action.equals(RESUME)) {
				((MultiplayerGameScene) SceneManager.getInstance().getCurrentScene()).resumeGame();
			}
		}
	}

	public void sendMessage(String message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			Debug.e(e);
		}
	}
}
