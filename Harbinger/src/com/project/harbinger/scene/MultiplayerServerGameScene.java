package com.project.harbinger.scene;

import java.io.IOException;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.project.harbinger.multiplayer.BluetoothServer;

public class MultiplayerServerGameScene extends GameScene {
	
	private BluetoothServer server;
	
	public MultiplayerServerGameScene(BluetoothServer server) {
		super();
		
		this.server = server;
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
		
		registerUpdateHandler(createServerUpdateHandler());
	}
	
	protected IUpdateHandler createServerUpdateHandler() {
		IUpdateHandler iUpdateHandler = new IUpdateHandler() {

			float x = 0;
			float a = 1f / 25f;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				x += pSecondsElapsed;
				if (x >= a) {
					Debug.e("Wysła");
				//	server.sendGameState(gameObjects);
					x = 0;
				}
			}

			@Override
			public void reset() {
			}
			
		};
		
		return iUpdateHandler;
	}
	
}
