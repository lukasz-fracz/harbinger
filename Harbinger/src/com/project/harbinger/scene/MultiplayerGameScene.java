package com.project.harbinger.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.level.IEntityLoader;
import org.andengine.util.level.LevelLoader;
import org.xml.sax.Attributes;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.project.harbinger.gameObject.ActiveEnemy;
import com.project.harbinger.gameObject.Bullet;
import com.project.harbinger.gameObject.Cruiser;
import com.project.harbinger.gameObject.GameObject;
import com.project.harbinger.gameObject.HeavyFighter;
import com.project.harbinger.gameObject.LightFighter;
import com.project.harbinger.gameObject.Meteor;
import com.project.harbinger.gameObject.Missile;
import com.project.harbinger.gameObject.Player;
import com.project.harbinger.gameObject.StaticEnemy;
import com.project.harbinger.gameObject.ActiveEnemy.ActiveEnemyType;
import com.project.harbinger.gameObject.Missile.MissileType;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.multiplayer.BluetoothConnection;

public class MultiplayerGameScene extends GameScene {

private BluetoothConnection bluetoothConnection;
private boolean isClient;
private Sprite player2;
	
	public MultiplayerGameScene(BluetoothConnection bluetoothConnection, boolean isClient) {
		super();
		
		this.bluetoothConnection = bluetoothConnection;
		this.isClient = isClient;
		createPhysics();
		try {
			loadLevel(0);
		} catch (IOException e) {}
	}
	
	@Override
	public void createScene() {
		score = 0;
		currentLevel = 0;
		lifes = 5;
		isPaused = false;
		
		createBackground();
		createHUD();
	}
	
	protected void createPauseMenu() {
		gamePausedText = new Text(10, 10, resourcesManager.getFont(),
				"Game paused", new TextOptions(HorizontalAlign.LEFT), vbom);
		gamePausedText.setPosition(40, 200);
		gamePausedText.setColor(Color.GREEN);
		

		backButton = new Sprite(100, 400, ResourcesManager.getInstance().getBackButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            SceneManager.getInstance().loadMenuScene(engine);
	        	// TODO wiadomo
	            return true;
	        };
	    };
	    resumeButton = new Sprite(100, 300, ResourcesManager.getInstance().getResumeButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            gameHUD.detachChild(gamePausedText);
	            gameHUD.detachChild(backButton);
	            gameHUD.detachChild(this);
	            gameHUD.unregisterTouchArea(this);
	            gameHUD.unregisterTouchArea(backButton);
	            isPaused = false;
	            bluetoothConnection.sendMessage(BluetoothConnection.RESUME + BluetoothConnection.DASH);
	            return true;
	        };
	    };
	}
	
	@Override
	public void onBackKeyPressed() {
		super.onBackKeyPressed();
		
		if (isPaused) {
			bluetoothConnection.sendMessage(BluetoothConnection.PAUSE + BluetoothConnection.DASH);
		} else {
			bluetoothConnection.sendMessage(BluetoothConnection.RESUME + BluetoothConnection.DASH);
		}
	}
	
	protected void createPhysics() {
		super.createPhysics(25);
		
		registerUpdateHandler(createMultiplayerUpdateHandler());
	}
	
	protected IUpdateHandler createMultiplayerUpdateHandler() {
		IUpdateHandler iUpdateHandler = new IUpdateHandler() {

			float x = 0;
			float a = 1f / 25f;
			
			float oldX = -1;
			float oldY = -1;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				x += pSecondsElapsed;
				if (x >= a) {
					if (!(player.getX() == oldX && player.getY() == oldY)) {
						bluetoothConnection.sendMessage(BluetoothConnection.MOVE + BluetoothConnection.DASH + player.getX() + 
								BluetoothConnection.SEMICOLON + player.getY());
						oldX = player.getX();
						oldY = player.getY();
					}
					x = 0;
				}
			}

			@Override
			public void reset() {
			}
			
		};
		
		return iUpdateHandler;
	}
	
	protected ContactListener createContactListener() {
		/*
		 * TODO
		 * 
		 * tu trzeba i tak czy siak napisać na nowo całe kolizje, bo nie można skorzystać z tego co jest w super,
		 * bo tutaj trzeba będzie rozróżniać który z graczy zestrzelił wroga
		 */
		Debug.e(String.valueOf(isClient));
		if (!isClient) {
			return super.createContactListener();
		}
		
		ContactListener contactListener = new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				final Fixture first = contact.getFixtureA();
				final Fixture second = contact.getFixtureB();
				
				String firstUD = (String) first.getBody().getUserData();
				String secondUD = (String) second.getBody().getUserData();
				
			}

			@Override
			public void endContact(Contact contact) {}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				final Fixture first = contact.getFixtureA();
				final Fixture second = contact.getFixtureB();
				String firstUD = (String) first.getBody().getUserData();
				String secondUD = (String) second.getBody().getUserData();

				if (firstUD.equals(Player.PLAYER_USER_DATA) || secondUD.equals(Player.PLAYER_USER_DATA)) {
					return;
				}
				
				
				if (first.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA) || 
						second.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA) ||
						first.getBody().getUserData().equals(WALL_TOP_USER_DATA) || 
						second.getBody().getUserData().equals(WALL_TOP_USER_DATA)) {
					contact.setEnabled(false);
				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
			
		};
		
		return contactListener;		
	}
	
	public void pauseGame() {
		isPaused = true;
		attachChild(gamePausedText);
	}
	
	public void resumeGame() {
		isPaused = false;
		detachChild(gamePausedText);
	}
	
	public void movePlayer2(float x, float y) {
		player2.setX(x);
		player2.setY(y);
	}
	
	public void addMissile(float x, float y) {
		GameObject missile = new Missile(x, y, vbom, camera, physicsWorld, MissileType.PLAYER2, -1);
		missile.setCullingEnabled(true);
		attachChild(missile);
		gameObjects.add(missile);
	}
	
	public void creteMissile(float x, float y, MissileType type) {
		super.creteMissile(x, y, type);

		if (type == MissileType.PLAYER1 || type == MissileType.PLAYER2) {
			bluetoothConnection.sendMessage(BluetoothConnection.MISSILE + BluetoothConnection.DASH + x + 
					BluetoothConnection.SEMICOLON + y);
		}
	}
	
	protected void loadLevel(int levelID) throws IOException {
		super.loadLevel(levelID);
		
		player2 = new Sprite(player.getX(), player.getY(), ResourcesManager.getInstance().getPlayer2Region(), vbom);
		attachChild(player2);
	}
}
