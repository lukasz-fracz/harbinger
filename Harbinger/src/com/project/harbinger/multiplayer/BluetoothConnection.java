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
	private MultiplayerGameScene gameScene;
	private boolean status;
	
	private static final String PAUSE = "pause";
	private static final String MOVE = "move";
	private static final String MISSILE = "missile";
	private static final String DESTROY = "destroy";
	private static final String RESUME = "resume";
	private static final String SCORE = "score";
	private static final String DASH = "-";
	private static final String SEMICOLON = ";";
	private static final String AT = "@";
	private static final String MYSCORE = "finished";
	private static final String LOADED = "loaded";
	private static final String DEAD = "dead";
	private static final String YES = "yes";
	private static final String NO = "no";
	
	public void setGameScene(MultiplayerGameScene gameScene) {
		this.gameScene = gameScene;
	}
	
	public void run() {
		status = true;
		
		while (status) {
			String message = "";
			try {
				message = (String) ois.readObject();
			} catch (Exception e) {
				Debug.e(e);
				status = false;
				break;
			}
			
			int dash = message.indexOf(DASH);
			String action = message.substring(0, dash);
			Debug.e(message);
			if (action.equals(MOVE)) {
				int semicolon = message.indexOf(SEMICOLON);
				float x = Float.valueOf(message.substring(dash + 1, semicolon));
				float y = Float.valueOf(message.substring(semicolon + 1));
				
				gameScene.movePlayer2(x, y);
			} else if (action.equals(MISSILE)) {
				int semicolon = message.indexOf(SEMICOLON);
				int at = message.indexOf(AT);
				float x = Float.valueOf(message.substring(dash + 1, semicolon));
				float y = Float.valueOf(message.substring(semicolon + 1, at));
				int id = Integer.valueOf(message.substring(at + 1));
				
				gameScene.addMissile(x, y, id);
			} else if (action.equals(PAUSE)) {
				gameScene.pauseGame();
			} else if (action.equals(RESUME)) {
				gameScene.resumeGame();
			} else if (action.equals(DESTROY)) {
				int id = Integer.valueOf(message.substring(dash + 1));
				gameScene.setToDestroy(id);
			} else if (action.equals(SCORE)) {
				int score = Integer.valueOf(message.substring(dash + 1));
				gameScene.addScore(score);
			} else if (action.equals(MYSCORE)) {
				int score = Integer.valueOf(message.substring(dash + 1));
				gameScene.updateOpponentScore(score);
			} else if (action.equals(DEAD)) {
				gameScene.partnerDead();
			} else if (action.equals(YES)) {
				gameScene.takeLife();
			} else if (action.equals(NO)) {
				gameScene.screwYou();
			} else if (action.equals(LOADED)) {
				gameScene.opponentIsReady();
			}
		}
		
		try {
			ois.close();
			oos.close();
		} catch (IOException e) {
			Debug.e(e);
		}
	}

	public void stopConnection() {
		status = false;
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
	
	public void sendMissile(float x, float y, int id) {
		sendMessage(MISSILE + DASH + x + SEMICOLON + y + AT + id);
	}
	
	public void sendDestroy(int id) {
		sendMessage(DESTROY + DASH + id);
	}
	
	public void sendScore(int score) {
		sendMessage(SCORE + DASH + score);
	}
	
	public void sendMyscore(int score) {
		sendMessage(MYSCORE + DASH + score);
	}
	
	public void sendLoaded() {
		sendMessage(LOADED + DASH);
	}
	
	public void sendDead() {
		sendMessage(DEAD + DASH);
	}
	
	public void sendYes() {
		sendMessage(YES + DASH);
	}
	
	public void sendNo() {
		sendMessage(NO + DASH);
	}
}
