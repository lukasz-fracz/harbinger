package com.project.harbinger.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.PhysicsConnector;
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
import com.project.harbinger.gameObject.Enemy;
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
	private int opponentScore;
	private Sprite noButton, yesButton;
	private Text partnerDeadText;

	
	public MultiplayerGameScene(BluetoothConnection bluetoothConnection, boolean isClient) {
		super();
		
		this.bluetoothConnection = bluetoothConnection;
		bluetoothConnection.setGameScene(this);
		bluetoothConnection.start();
		this.isClient = isClient;
		opponentScore = 0;
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
		createPartnerDeadMenu();
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
	            bluetoothConnection.sendResume();
	            return true;
	        };
	    };
	}
	
	private void createPartnerDeadMenu() {
		partnerDeadText = new Text(10, 10, resourcesManager.getFont(),
				"Partner's dead", new TextOptions(HorizontalAlign.LEFT), vbom);
		partnerDeadText.setPosition(40, 200);
		partnerDeadText.setColor(Color.GREEN);
		
		yesButton = new Sprite(100, 300, ResourcesManager.getInstance().getYesButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	        	gameHUD.detachChild(partnerDeadText);
	        	gameHUD.detachChild(yesButton);
	        	gameHUD.detachChild(noButton);
	        	gameHUD.unregisterTouchArea(yesButton);
	    		gameHUD.unregisterTouchArea(noButton);
	    		
	            bluetoothConnection.sendYes();
	            lifes--;
	            updateScore();
	            isPaused = false;
	            return true;
	        };
	    };
	    noButton = new Sprite(100, 400, ResourcesManager.getInstance().getNoButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            bluetoothConnection.sendNo();
	            gameFinished();
	            return true;
	        };
	    };
	}
	
	private void showPartnerDeadMenu() {
		isPaused = true;
		gameHUD.attachChild(partnerDeadText);
		gameHUD.attachChild(yesButton);
		gameHUD.attachChild(noButton);
		gameHUD.registerTouchArea(yesButton);
		gameHUD.registerTouchArea(noButton);
	}
	
	@Override
	public void onBackKeyPressed() {
		super.onBackKeyPressed();
		
		if (isPaused) {
			bluetoothConnection.sendPause();
		} else {
			bluetoothConnection.sendResume();
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
						bluetoothConnection.sendMove(player.getX(), player.getY());
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
		ContactListener contactListener;
		if (!isClient) {
			contactListener = super.createContactListener();
		} else {
			contactListener = new ContactListener() {

				@Override
				public void beginContact(Contact contact) {
					final Fixture first = contact.getFixtureA();
					final Fixture second = contact.getFixtureB();
					
					String firstUD = (String) first.getBody().getUserData();
					String secondUD = (String) second.getBody().getUserData();
					
					
					// activating active enemies
					if ((firstUD.equals(WALL_TOP_USER_DATA) && secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) || 
							(secondUD.equals(WALL_TOP_USER_DATA) && firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA))) {
						if (firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							first.getBody().setUserData(ActiveEnemy.ACTIVE_START_ME);
						} else {
							second.getBody().setUserData(ActiveEnemy.ACTIVE_START_ME);
							return;
						}
					}
					
					// bouncing active enemies
					
					// from walls
					if ((firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA) && secondUD.equals(WALL_VERTICAL_USER_DATA)) ||
							(secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA) && firstUD.equals(WALL_VERTICAL_USER_DATA))) {
						if (firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							first.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						} else {
							second.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						}
						return;
					}
					
					// from another active enemy
					if (firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA) && secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
						first.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						second.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						return;
					}
					
					// handling player collision with enemies, the player has to respawn, and the client has to inform server
					
					if (firstUD.equals(Player.PLAYER_USER_DATA)) {
						if (secondUD.equals(StaticEnemy.STATIC_USER_DATA) || secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							second.getBody().setUserData("COLLISION");
							first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							for (GameObject gobj : gameObjects) {
								if (gobj.getBody().getUserData().equals("COLLISION")) {
									gobj.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
									bluetoothConnection.sendDestroy(gobj.getId());
									return;
								}
							}
						}
					}
					if (secondUD.equals(Player.PLAYER_USER_DATA)) {
						if (firstUD.equals(StaticEnemy.STATIC_USER_DATA) || firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							first.getBody().setUserData("COLLISION");
							second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							for (GameObject gobj : gameObjects) {
								if (gobj.getBody().getUserData().equals("COLLISION")) {
									gobj.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
									bluetoothConnection.sendDestroy(gobj.getId());
									return;
								}
							}
						}
					}
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
					
					// if there is a collision with missile
					
					if (firstUD.equals(Missile.MISSILE_USER_DATA) || firstUD.equals(Missile.MISSILE_PLAYER2_USER_DATA) || 
							secondUD.equals(Missile.MISSILE_USER_DATA) || secondUD.equalsIgnoreCase(Missile.MISSILE_PLAYER2_USER_DATA)) {
						// set missile to destroy
						if (firstUD.equals(Missile.MISSILE_USER_DATA) || firstUD.equals(Missile.MISSILE_PLAYER2_USER_DATA)) {
							first.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
						}
						if (secondUD.equals(Missile.MISSILE_USER_DATA) || secondUD.equals(Missile.MISSILE_PLAYER2_USER_DATA)) {
							second.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
						}
						contact.setEnabled(false);
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
		}
		return contactListener;		
	}
	
	protected void deleteObjectsForDestroy() {
		if (isClient) {
			super.deleteObjectsForDestroy();
			return;
		}
		
		if (physicsWorld != null) {
			Iterator<GameObject> objects = gameObjects.iterator();
			
			while (objects.hasNext()) {
				GameObject next = objects.next();
				if (next.getBody() != null && 
						next.getBody().getUserData().equals(GameObject.DESTROY_USER_DATA) || 
						next.getBody().getUserData().equals(GameObject.DESTROY_BY_WALL_USER_DATA) ||
						next.getBody().getUserData().equals(GameObject.DESTROY_BY_SECOND_PLAYER)) {
					PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().
							findPhysicsConnectorByShape(next);
					if (physicsConnector != null) {
						if (next instanceof Player) {
							next.getBody().setUserData(Player.PLAYER_USER_DATA);
							respawnPlayer();
							return;
						}
						if (!(next instanceof Missile)) {
							bluetoothConnection.sendDestroy(next.getId());
						}
						if (next instanceof Enemy) {
							enemies--;
						}
						if (next.getBody().getUserData().equals(GameObject.DESTROY_USER_DATA)) {
							score += next.getScore();
							updateScore();
						} else if (next.getBody().getUserData().equals(GameObject.DESTROY_BY_SECOND_PLAYER)) {
							bluetoothConnection.sendScore(next.getScore());
							opponentScore += next.getScore();
						}
						
						physicsWorld.unregisterPhysicsConnector(physicsConnector);
						next.getBody().setActive(false);
						physicsWorld.destroyBody(next.getBody());
						detachChild(next);
						objects.remove();
					}
				}
			}
		}
		if (!isClient) {
			bluetoothConnection.sendMyscore(score);
		}
	}
	
	protected void showGameOverText() {
		isPaused = true;
		gameHUD.attachChild(gameOverText);
		bluetoothConnection.sendDead();
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
		GameObject missile = new Missile(x, y, vbom, camera, physicsWorld, MissileType.PLAYER2);
		missile.setCullingEnabled(true);
		attachChild(missile);
		gameObjects.add(missile);
	}
	
	public void setToDestroy(int id) {
		for (GameObject gobj : gameObjects) {
			if (gobj.getId() == id) {
				gobj.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
				return;
			}
		}
	}
	
	public void addScore(int score) {
		this.score += score;
		updateScore();
	}
	
	public void updateOpponentScore(int opponentScore) {
		this.opponentScore = opponentScore;
	}
	
	public void partnerDead() {
		showPartnerDeadMenu();
	}
	
	public void takeLife() {
		gameHUD.detachChild(gameOverText);
		lifes++;
		isPaused = false;
	}
	
	public void screwYou() {
		gameFinished();
	}
	
	public void creteMissile(float x, float y, MissileType type) {
		super.creteMissile(x, y, type);

		if (type == MissileType.PLAYER1 || type == MissileType.PLAYER2) {
			bluetoothConnection.sendMissile(x, y);
		}
	}
	
	protected void loadLevel(int levelID) throws IOException {
		super.loadLevel(levelID);
		
		try {
			detachChild(player2);
		} catch (Exception e) {}
		
		player2 = new Sprite(player.getX(), player.getY(), ResourcesManager.getInstance().getPlayer2Region(), vbom);
		attachChild(player2);
	}
	
	protected void gameFinished() {
		Debug.e("Koniec. Mój wynik: " + score + " Wynik tego drugiego: " + opponentScore);
		SceneManager.getInstance().loadMultiplayerGameCompletedScene(engine, score, opponentScore);
	}
}
