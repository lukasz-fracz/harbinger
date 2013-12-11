package com.project.harbinger.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.project.harbinger.gameObject.GameObject;
import com.project.harbinger.gameObject.Missile;
import com.project.harbinger.gameObject.Missile.MissileType;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.multiplayer.BluetoothConnection;

public class MultiplayerGameScene extends GameScene {

private BluetoothConnection bluetoothConnection;
	
	public MultiplayerGameScene(BluetoothConnection bluetoothConnection) {
		super();
		
		this.bluetoothConnection = bluetoothConnection;
	}
	
	@Override
	public void createScene() {
		score = 0;
		currentLevel = 0;
		lifes = 5;
		isPaused = false;
		
		createBackground();
		createHUD();
		createPhysics();
		try {
			loadLevel(0);
		} catch (IOException e) {}
	}
	
	@Override
	public void onBackKeyPressed() {
		super.onBackKeyPressed();
	}
	
	protected void createPhysics() {
		super.createPhysics(25);
	}
	
	
	public synchronized void setPlayerVelocity(float x, float y) {
		GameObject missile = new Missile(x, y, vbom, camera, physicsWorld, MissileType.PLAYER);
		missile.setCullingEnabled(true);
		attachChild(missile);
		gameObjects.add(missile);
		//creteMissile(x, y, MissileType.PLAYER);
		//creteMissile(x, y, MissileType.PLAYER);
	}
	
	public void creteMissile(float x, float y, MissileType type) {
		Debug.e("DUPAPAPAPA");
		GameObject missile = new Missile(x, y, vbom, camera, physicsWorld, type);
		missile.setCullingEnabled(true);
		attachChild(missile);
		gameObjects.add(missile);
		if (type == MissileType.PLAYER) {
			bluetoothConnection.sendMessage((player.getX() + 10) + "-" + (player.getY() - 35));
		}
	}
}
