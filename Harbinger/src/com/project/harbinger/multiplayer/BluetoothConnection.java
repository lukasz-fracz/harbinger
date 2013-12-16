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
	
	private static final String PAUSE = "pause";
	private static final String MOVE = "move";
	private static final String MISSILE = "missile";
	private static final String DESTROY = "destroy";
	private static final String RESUME = "resume";
	private static final String SCORE = "score";
	private static final String DASH = "-";
	private static final String SEMICOLON = ";";
	
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
			} else if (action.equals(DESTROY)) {
				int id = Integer.valueOf(message.substring(dash + 1));
				((MultiplayerGameScene) SceneManager.getInstance().getCurrentScene()).setToDestroy(id);
			} else if (action.equals(SCORE)) {
				int score = Integer.valueOf(message.substring(dash + 1));
				((MultiplayerGameScene) SceneManager.getInstance().getCurrentScene()).addScore(score);
			}
		}
	}

	private synchronized void sendMessage(String message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	
	public void sendPause() {
		sendMessage(PAUSE + DASH);
	}
	
	public void sendResume() {
		sendMessage(RESUME + DASH);
	}
	
	public void sendMove(float x, float y) {
		sendMessage(MOVE + DASH + x + SEMICOLON + y);
	}
	
	public void sendMissile(float x, float y) {
		sendMessage(MISSILE + DASH + x + SEMICOLON + y);
	}
	
	public void sendDestroy(int id) {
		sendMessage(DESTROY + DASH + id);
	}
	
	public void sendScore(int score) {
		sendMessage(SCORE + DASH + score);
	}
}
