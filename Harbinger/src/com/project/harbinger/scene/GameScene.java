package com.project.harbinger.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.level.IEntityLoader;
import org.andengine.util.level.LevelLoader;
import org.xml.sax.Attributes;

import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.project.harbinger.gameObject.ActiveEnemy;
import com.project.harbinger.gameObject.ActiveEnemy.ActiveEnemyType;
import com.project.harbinger.gameObject.Bullet;
import com.project.harbinger.gameObject.Cruiser;
import com.project.harbinger.gameObject.Enemy;
import com.project.harbinger.gameObject.GameObject;
import com.project.harbinger.gameObject.HeavyFighter;
import com.project.harbinger.gameObject.LightFighter;
import com.project.harbinger.gameObject.Meteor;
import com.project.harbinger.gameObject.Missile;
import com.project.harbinger.gameObject.Missile.MissileType;
import com.project.harbinger.gameObject.Player;
import com.project.harbinger.gameObject.StaticEnemy;
import com.project.harbinger.manager.ResourcesManager;
import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

public class GameScene extends BaseScene {

	protected HUD gameHUD;
	protected Text scoreText, gameOverText, levelCompletedText, gamePausedText;
	protected PhysicsWorld physicsWorld;
	//private Text debugPlayerCoordinates;
	protected int score, lifes, currentLevel, enemies;
	protected boolean isPaused;
	protected Sprite backButton, resumeButton;
	int missileCounter;
	
	@Override
	public void createScene() {
		score = 0;
		currentLevel = 0;
		lifes = 5;
		isPaused = false;
		
		createBackground();
		createHUD();
		createPhysics(60);
		try {
			loadLevel(0);
		} catch (IOException e) {}
	}

	private void showPauseMenu() {
		gameHUD.attachChild(gamePausedText);
		
		
	    gameHUD.registerTouchArea(resumeButton);
	    gameHUD.registerTouchArea(backButton);
	    gameHUD.attachChild(backButton);
	    gameHUD.attachChild(resumeButton);
	}
	
	@Override
	public void onBackKeyPressed() {
		if (isPaused) {
			isPaused = false;
			gameHUD.detachChild(gamePausedText);
            gameHUD.detachChild(backButton);
            gameHUD.detachChild(resumeButton);
            gameHUD.unregisterTouchArea(resumeButton);
            gameHUD.unregisterTouchArea(backButton);
			return;
		}
		isPaused = true;
		showPauseMenu();
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(240, 400);
		camera.setChaseEntity(null);
	}

	protected void createBackground() {
		setBackground(new Background(Color.BLACK));
	}
	
	private void loadNextLevel(int fps) {
		isPaused = true;
		showLevelCompleted();
		detachChild(player);
		currentLevel++;
		createPhysics(fps);
		try {
			loadLevel(currentLevel);
		} catch (IOException e) {
			enemies = -1;
			gameFinished();
		}
	}
	
	protected void showLevelCompleted() {
		gameHUD.attachChild(levelCompletedText);
		setOnSceneTouchListener(new IOnSceneTouchListener() {

			@Override
			public boolean onSceneTouchEvent(Scene pScene,
					TouchEvent pSceneTouchEvent) {
				gameHUD.detachChild(levelCompletedText);
				isPaused = false;
				return false;
			}
			
		});
	}
	
	protected void gameFinished() {
		SceneManager.getInstance().loadGameCompletedScene(engine, score);
	}
	
	protected void createHUD() {
		gameHUD = new HUD();
		
		scoreText = new Text(20, 240, resourcesManager.getFont(),
				"Score: 0123456789\nLifes: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText.setPosition(0, 0);
		scoreText.setText("Score: 0\nLifes: " + String.valueOf(lifes));
		gameOverText = new Text(10, 10, resourcesManager.getFont(),
				"You're dead...", new TextOptions(HorizontalAlign.LEFT), vbom);
		gameOverText.setPosition(40, 400);
		gameOverText.setColor(Color.RED);
		
		levelCompletedText = new Text(10, 10, resourcesManager.getFont(),
				"   Level\ncompleted!", new TextOptions(HorizontalAlign.LEFT), vbom);
		levelCompletedText.setPosition(40, 300);
		levelCompletedText.setColor(Color.BLUE);		
		
		gameHUD.attachChild(scoreText);
		
		/*debugPlayerCoordinates = new Text(10, 10, resourcesManager.getFont(),
				"x: 1234567890- y: 1234567890-", new TextOptions(HorizontalAlign.LEFT), vbom);
		debugPlayerCoordinates.setPosition(0, 700);
		debugPlayerCoordinates.setText("x: y:");
		debugPlayerCoordinates.setSize(300, 100);
		gameHUD.attachChild(debugPlayerCoordinates);*/

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(50, 650, camera, 
				ResourcesManager.getInstance().getAnalogBackgroundRegion(), 
				ResourcesManager.getInstance().getAnalogRegion(), 0.1f, 200, vbom, 
				new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				player.setVelocity(10 * pValueX, 10 * pValueY);
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
			}
		});
		//analogOnScreenControl.getControlBase().setBlendFunction(2, 4);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();
		
		
		setChildScene(analogOnScreenControl);
		
		
	    final Sprite fire = new Sprite(300, 670, ResourcesManager.getInstance().getFireButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	        	if (touchEvent.getAction() == TouchEvent.ACTION_DOWN) {
	        		player.fire();
	        	}
	            //creteMissile(player.getX() + 10, player.getY() - 35, MissileType.PLAYER);
	            return true;
	        };
	    };
	   
	    gameHUD.registerTouchArea(fire);
	    gameHUD.attachChild(fire);
	    
		camera.setHUD(gameHUD);
		
		createPauseMenu();
	}
	
	protected void createPauseMenu() {
		gamePausedText = new Text(10, 10, resourcesManager.getFont(),
				"Game paused", new TextOptions(HorizontalAlign.LEFT), vbom);
		gamePausedText.setPosition(40, 200);
		gamePausedText.setColor(Color.GREEN);
		

		backButton = new Sprite(100, 400, ResourcesManager.getInstance().getBackButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            SceneManager.getInstance().loadMenuScene(engine);
	        	
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
	            return true;
	        };
	    };
	}
	
	protected void showGameOverText() {
		isPaused = true;
		gameHUD.attachChild(gameOverText);
		setOnSceneTouchListener(new IOnSceneTouchListener() {

			@Override
			public boolean onSceneTouchEvent(Scene pScene,
					TouchEvent pSceneTouchEvent) {
				SceneManager.getInstance().loadMenuScene(engine);
				return false;
			}
			
		});
	}
	
	/*public void debugSetPlayerCoordinates(float x, float y) {
		String xC = "x: " + String.valueOf(x);
		String yC = "y: " + String.valueOf(y);
		
		debugPlayerCoordinates.setText(xC + "\n" + yC);
	}*/
	
	protected void updateScore() {
		scoreText.setText("Score: " + String.valueOf(score) + "\nLifes: " + String.valueOf(lifes));
	}
	
	protected void createPhysics(int fps) {
		physicsWorld = new FixedStepPhysicsWorld(fps, new Vector2(0, 0), false);
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(createContactListener());
		registerUpdateHandler(createIUpdateHandler());
		createBounds();
	}
	
	protected static final String WALL_VERTICAL_USER_DATA = "wallV";
	protected static final String WALL_TOP_USER_DATA = "WALL-E";
	protected static final String WALL_BOTTOM_USER_DATA = "EVA";
	
	protected void createBounds() {
		Body body;
		final Rectangle wall_bottom = new Rectangle(0, 590, 480, 10, vbom);
		body = PhysicsFactory.createBoxBody(physicsWorld, wall_bottom, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
	    body.setUserData(WALL_BOTTOM_USER_DATA);
	    attachChild(wall_bottom);
	    final Rectangle wall_top = new Rectangle(0, -10, 480, 10, vbom);
		body = PhysicsFactory.createBoxBody(physicsWorld, wall_top, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
	    body.setUserData(WALL_TOP_USER_DATA);
	    attachChild(wall_top);
	    final Rectangle wall_left = new Rectangle(-10, -3000, 10, 8000, vbom);
		body = PhysicsFactory.createBoxBody(physicsWorld, wall_left, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
	    body.setUserData(WALL_VERTICAL_USER_DATA);
	    attachChild(wall_left);
	    final Rectangle wall_right = new Rectangle(480, -3000, 10, 8000, vbom);
		body = PhysicsFactory.createBoxBody(physicsWorld, wall_right, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
	    body.setUserData(WALL_VERTICAL_USER_DATA);
	    attachChild(wall_right);
	}
	
	protected IUpdateHandler createIUpdateHandler() {
		IUpdateHandler iUpdateHandler = new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				deleteObjectsForDestroy();
				updateActiveEnemies();
				if (enemies == 0) {
					loadNextLevel(30);
				}
			}

			@Override
			public void reset() {
			}
			
		};
		
		return iUpdateHandler;
	}
	
	public void onManagedUpdate(float pSecondsElapsed) {
		if (isPaused) {
			super.onManagedUpdate(0);
		} else {
			super.onManagedUpdate(pSecondsElapsed);
		}
	}

	protected ContactListener createContactListener() {
		ContactListener contactListener = new ContactListener() {

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
				
				// missile hit something
				if (firstUD.equals(Missile.MISSILE_USER_DATA) || firstUD.equals(Missile.MISSILE_PLAYER2_USER_DATA)) {
					if (!secondUD.equals(WALL_BOTTOM_USER_DATA) && !secondUD.equals(WALL_TOP_USER_DATA)) {
						if (firstUD.equals(Missile.MISSILE_USER_DATA)) {
							second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
						} else {
							second.getBody().setUserData(GameObject.DESTROY_BY_SECOND_PLAYER);
						}
					}
					first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
					return;
				}
				
				if (secondUD.equals(Missile.MISSILE_USER_DATA) || secondUD.equals(Missile.MISSILE_PLAYER2_USER_DATA)) {
					if (!firstUD.equals(WALL_BOTTOM_USER_DATA) && !firstUD.equals(WALL_TOP_USER_DATA)) {
						if (secondUD.equals(Missile.MISSILE_USER_DATA)) {
							first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
						} else {
							first.getBody().setUserData(GameObject.DESTROY_BY_SECOND_PLAYER);
						}
					}
					second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
					return;
				}
				
				// handling player collision with enemies, the player has to respawn, and the client has to inform server
				
				if (firstUD.equals(Player.PLAYER_USER_DATA)) {
					if (secondUD.equals(StaticEnemy.STATIC_USER_DATA) || secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
						second.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
						first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
					}
					return;
				}
				if (secondUD.equals(Player.PLAYER_USER_DATA)) {
					if (firstUD.equals(StaticEnemy.STATIC_USER_DATA) || firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
						first.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
						second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
					}
					return;
				}			
				
				// destroying enemies who touch bottom wall
				if (firstUD.equals(WALL_BOTTOM_USER_DATA)) {
					if (secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA) || secondUD.equals(StaticEnemy.STATIC_USER_DATA) ||
							secondUD.equals(Missile.MISSILE_USER_DATA)) {
						second.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
					}
					return;
				}
				if (secondUD.equals(WALL_BOTTOM_USER_DATA)) {
					if (firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA) || firstUD.equals(StaticEnemy.STATIC_USER_DATA) ||
							firstUD.equals(Missile.MISSILE_USER_DATA)) {
						first.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
					}
					return;
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
	
	protected void respawnPlayer() {
		lifes--;
		updateScore();
		
		if (lifes == 0) {
			showGameOverText();
			return;
		}	
		
		float width = player.getWidth() / 2;
        float heigh = player.getHeight() / 2;
        float angle = player.getBody().getAngle();
        player.getBody().setTransform((300 + width) / 32, (200 + heigh) / 32, angle);
        player.setVelocity(0, 0);
	}
	
	protected void deleteObjectsForDestroy() {
		if (physicsWorld != null) {
			Iterator<GameObject> objects = gameObjects.iterator();
			
			while (objects.hasNext()) {
				GameObject next = objects.next();
				if (next.getBody() != null && 
						next.getBody().getUserData().equals(GameObject.DESTROY_USER_DATA) || 
						next.getBody().getUserData().equals(GameObject.DESTROY_BY_WALL_USER_DATA)) {
					PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().
							findPhysicsConnectorByShape(next);
					if (physicsConnector != null) {
						if (next instanceof Player) {
							next.getBody().setUserData(Player.PLAYER_USER_DATA);
							respawnPlayer();
							return;
						}
						if (next instanceof Enemy) {
							enemies--;
						}
						if (next.getBody().getUserData().equals(GameObject.DESTROY_USER_DATA)) {
							score += next.getScore();
							updateScore();
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
	}
	
	private void updateActiveEnemies() {
		for (GameObject object : gameObjects) {
			if (object.getBody().getUserData().equals(ActiveEnemy.ACTIVE_START_ME)) {
				((ActiveEnemy) object).start();
			} else if (object.getBody().getUserData().equals(ActiveEnemy.ACTIVE_TURN)) {
				((ActiveEnemy) object).changeSide();
			}
		}
	}
	
	// level loading
	private static final String TAG_LEVEL_ATTRIBUTE_ENEMIES = "enemies";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	private static final String TAG_ENTITY_ATTRIBUTE_ID = "id";
	private static final String TAG_ENTITY = "entity";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_METEOR = "meteor";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BULLET = "bullet";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_LIGHT_FIGHTER = "left-light-fighter";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_LIGHT_FIGHTER = "right-light-fighter";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_HEAVY_FIGHTER = "left-heavy-fighter";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_HEAVY_FIGHTER = "right-heavy-fighter";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_CRUISER = "left-cruiser";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_CRUISER = "right-cruiser";
	
	
	protected Player player;
	protected List<GameObject> gameObjects;

	protected void loadLevel(int levelID) throws IOException {
		gameObjects = new ArrayList<GameObject>();
		missileCounter = -1;
		
	    final LevelLoader levelLoader = new LevelLoader("");
	    
	    levelLoader.registerEntityLoader("level", new IEntityLoader() {
	        public IEntity onLoadEntity(final String pEntityName, final Attributes pAttributes) {
	            enemies = SAXUtils.getIntAttributeOrThrow(
	            		pAttributes, TAG_LEVEL_ATTRIBUTE_ENEMIES);

	            return GameScene.this;
	        }
	    });
	    
	    levelLoader.registerEntityLoader(TAG_ENTITY, new IEntityLoader() {
	    	        public IEntity onLoadEntity(final String pEntityName, final Attributes pAttributes) {
	    	            final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
	    	            final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
	    	            final int id = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_ID);
	    	            final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	    	            
	    	            final GameObject levelObject;
	    	            
	    	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
	    	                player = new Player(x, y, vbom, camera, physicsWorld, GameScene.this);
	    	                levelObject = player;
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_METEOR)) {
	    	            	levelObject = new Meteor(x, y, vbom, camera, physicsWorld, id);
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BULLET)) {
	    	            	levelObject = new Bullet(x, y, vbom, camera, physicsWorld, id);
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_LIGHT_FIGHTER)) {
	    	            	levelObject = new LightFighter(x, y, vbom, camera, physicsWorld, ActiveEnemyType.LEFT, id);
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_LIGHT_FIGHTER)) {
	    	            	levelObject = new LightFighter(x, y, vbom, camera, physicsWorld, ActiveEnemyType.RIGHT, id);
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_HEAVY_FIGHTER)) {
	    	            	levelObject = new HeavyFighter(x, y, vbom, camera, physicsWorld, ActiveEnemyType.LEFT, id);
	    	            } else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_HEAVY_FIGHTER)) {
	    	            	levelObject = new HeavyFighter(x, y, vbom, camera, physicsWorld, ActiveEnemyType.RIGHT, id);
	    	            } else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_CRUISER)) {
	    	            	levelObject = new Cruiser(x, y, vbom, camera, physicsWorld, ActiveEnemyType.LEFT, id);
	    	            } else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_CRUISER)) {
	    	            	levelObject = new Cruiser(x, y, vbom, camera, physicsWorld, ActiveEnemyType.RIGHT, id);
	    	            } else {
	    	            	levelObject = null;
	    	            }
	    	            
	    	            gameObjects.add(levelObject);

	    	            levelObject.setCullingEnabled(true);
	    	            

	    	            return levelObject;
	    	        }
	    	    });
	    
	    levelLoader.loadLevelFromAsset(activity.getAssets(), "levels/" + currentLevel + ".lvl");
	}
	
	public void creteMissile(float x, float y, MissileType type) {
		GameObject missile = new Missile(x, y, vbom, camera, physicsWorld, type, missileCounter);
		missile.setCullingEnabled(true);
		attachChild(missile);
		gameObjects.add(missile);
		missileCounter--;
	}
}
