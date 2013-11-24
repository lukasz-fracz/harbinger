package com.project.harbinger.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
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

	private HUD gameHUD;
	private Text scoreText, gameOverText, levelCompletedText, gamePausedText;
	private PhysicsWorld physicsWorld;
	//private Text debugPlayerCoordinates;
	private int score, lifes, currentLevel, enemies;
	private boolean isPaused;
	
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

	private void showPauseMenu() {
		gameHUD.attachChild(gamePausedText);
		final Sprite backButton = new Sprite(100, 400, ResourcesManager.getInstance().getBackButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            SceneManager.getInstance().loadMenuScene(engine);
	        	
	            return true;
	        };
	    };
	    final Sprite resumeButton = new Sprite(100, 300, ResourcesManager.getInstance().getResumeButtonRegion(), vbom) {
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
		
	    gameHUD.registerTouchArea(resumeButton);
	    gameHUD.registerTouchArea(backButton);
	    gameHUD.attachChild(backButton);
	    gameHUD.attachChild(resumeButton);
	}
	
	@Override
	public void onBackKeyPressed() {
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

	private void createBackground() {
		setBackground(new Background(Color.BLACK));
	}
	
	private void loadNextLevel() {
		isPaused = true;
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
		
		detachChild(player);
		currentLevel++;
		createPhysics();
		try {
			loadLevel(currentLevel);
		} catch (IOException e) {
			SceneManager.getInstance().loadGameCompletedScene(engine);
		}
	}
	
	private void createHUD() {
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
		
		gamePausedText = new Text(10, 10, resourcesManager.getFont(),
				"Game paused", new TextOptions(HorizontalAlign.LEFT), vbom);
		gamePausedText.setPosition(40, 200);
		gamePausedText.setColor(Color.GREEN);
		
		
		gameHUD.attachChild(scoreText);
		
		/*debugPlayerCoordinates = new Text(10, 10, resourcesManager.getFont(),
				"x: 1234567890- y: 1234567890-", new TextOptions(HorizontalAlign.LEFT), vbom);
		debugPlayerCoordinates.setPosition(0, 700);
		debugPlayerCoordinates.setText("x: y:");
		debugPlayerCoordinates.setSize(300, 100);
		gameHUD.attachChild(debugPlayerCoordinates);*/
		
		final Sprite left = new Sprite(10, 675, ResourcesManager.getInstance().getLeftButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            if (touchEvent.isActionDown()) {
	            	player.setVelocity(-10, 0);
	            } else if (touchEvent.isActionUp() || 
	            		touchEvent.getMotionEvent().getActionMasked() == MotionEvent.ACTION_MOVE) {
	            	player.setVelocity(0, 0);
	            }	            
	            
	            return true;
	        };
	    };
		
	    final Sprite right = new Sprite(110, 675, ResourcesManager.getInstance().getRightButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            if (touchEvent.isActionDown()) {
	            	player.setVelocity(+10, 0);
	            } else if (touchEvent.isActionUp() || 
	            		touchEvent.getMotionEvent().getActionMasked() == MotionEvent.ACTION_MOVE) {
	            	player.setVelocity(0, 0);
	            }
	            return true;
	        };
	    };
	    
	    final Sprite up = new Sprite(60, 625, ResourcesManager.getInstance().getUpButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            if (touchEvent.isActionDown()) {
	            	player.setVelocity(0, -10);
	            } else if (touchEvent.isActionUp() || 
	            		touchEvent.getMotionEvent().getActionMasked() == MotionEvent.ACTION_MOVE) {
	            	player.setVelocity(0, 0);
	            }
	            return true;
	        };
	    };
	    
	    final Sprite down = new Sprite(60, 725, ResourcesManager.getInstance().getDownButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            if (touchEvent.isActionDown()) {
	            	player.setVelocity(0, 10);
	            } else if (touchEvent.isActionUp() || 
	            		touchEvent.getMotionEvent().getActionMasked() == MotionEvent.ACTION_MOVE) {
	            	player.setVelocity(0, 0);
	            }
	            return true;
	        };
	    };
	    final Sprite fire = new Sprite(300, 670, ResourcesManager.getInstance().getFireButtonRegion(), vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            creteMissile(player.getX() + 10, player.getY() - 35, MissileType.PLAYER);
	            return true;
	        };
	    };
	    
	    gameHUD.attachChild(left);
	    gameHUD.registerTouchArea(left);
	    gameHUD.attachChild(right);
	    gameHUD.registerTouchArea(right);
	    gameHUD.attachChild(up);
	    gameHUD.attachChild(down);
	    gameHUD.registerTouchArea(up);
	    gameHUD.registerTouchArea(down);
	    gameHUD.registerTouchArea(fire);
	    gameHUD.attachChild(fire);
	    
		camera.setHUD(gameHUD);
	}
	
	private void showGameOverText() {
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
	
	private void updateScore() {
		scoreText.setText("Score: " + String.valueOf(score) + "\nLifes: " + String.valueOf(lifes));
	}
	
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(createContactListener());
		registerUpdateHandler(createIUpdateHandler());
		createBounds();
	}
	
	private static final String WALL_VERTICAL_USER_DATA = "wallV";
	private static final String WALL_TOP_USER_DATA = "WALL-E";
	private static final String WALL_BOTTOM_USER_DATA = "EVA";
	
	private void createBounds() {
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
	
	private IUpdateHandler createIUpdateHandler() {
		IUpdateHandler iUpdateHandler = new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				deleteObjectsForDestroy();
				updateActiveEnemies();
				if (enemies == 0) {
					loadNextLevel();
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
	
	private ContactListener createContactListener() {
		ContactListener contactListener = new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				final Fixture first = contact.getFixtureA();
				final Fixture second = contact.getFixtureB();
				
				String firstUD = (String) first.getBody().getUserData();
				String secondUD = (String) second.getBody().getUserData();
				
				if (firstUD.equals(Player.PLAYER_USER_DATA) || secondUD.equals(Player.PLAYER_USER_DATA)) {
					if (firstUD.equals(Player.PLAYER_USER_DATA)) {
						if (secondUD.equals(StaticEnemy.STATIC_USER_DATA) || 
								secondUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							return;
						}
					} else {
						if (firstUD.equals(StaticEnemy.STATIC_USER_DATA) || 
								firstUD.equals(ActiveEnemy.ACTIVE_USER_DATA)) {
							second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
							return;
						}
					}
				}

				if (first.getBody().getUserData().equals(ActiveEnemy.ACTIVE_USER_DATA) && 
						second.getBody().getUserData().equals(ActiveEnemy.ACTIVE_USER_DATA)) {
					first.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
					second.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
					return;
				}
				
				if (first.getBody().getUserData().equals(Player.PLAYER_USER_DATA) ||
					second.getBody().getUserData().equals(Player.PLAYER_USER_DATA)) {
					
				}
				
				if (first.getBody().getUserData().equals(Missile.MISSILE_USER_DATA) || 
						second.getBody().getUserData().equals(Missile.MISSILE_USER_DATA)) {
					if (!first.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA)
							&& !first.getBody().getUserData().equals(WALL_TOP_USER_DATA)) {
						first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
						return;
					}
					if (!second.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA)
							&& !second.getBody().getUserData().equals(WALL_TOP_USER_DATA)) {
						second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
						return;
					}
				}
				
				if ((first.getBody().getUserData().equals(WALL_TOP_USER_DATA) && 
						second.getBody().getUserData().equals(ActiveEnemy.ACTIVE_USER_DATA)) || 
						(second.getBody().getUserData().equals(WALL_TOP_USER_DATA) && 
								first.getBody().getUserData().equals(ActiveEnemy.ACTIVE_USER_DATA))) {
					if (first.getBody().getUserData().equals(ActiveEnemy.ACTIVE_USER_DATA)) {
						first.getBody().setUserData(ActiveEnemy.ACTIVE_START_ME);
						return;
					} else {
						second.getBody().setUserData(ActiveEnemy.ACTIVE_START_ME);
						return;
					}
				}
				
				if ((first.getBody().getUserData().equals(WALL_VERTICAL_USER_DATA) && 
						second.getBody().getUserData().equals(ActiveEnemy.ACTIVE_USER_DATA)) || 
						(second.getBody().getUserData().equals(WALL_VERTICAL_USER_DATA) && 
								first.getBody().getUserData().equals(ActiveEnemy.ACTIVE_USER_DATA))) {
					if (first.getBody().getUserData().equals(ActiveEnemy.ACTIVE_USER_DATA)) {
						first.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						return;
					} else {
						second.getBody().setUserData(ActiveEnemy.ACTIVE_TURN);
						return;
					}
				}
				
				if (first.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA) || 
						second.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA)) {
					if (!first.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA) && 
							!first.getBody().getUserData().equals(Player.PLAYER_USER_DATA)) {
						first.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
						return;
					}
					if (!second.getBody().getUserData().equals(WALL_BOTTOM_USER_DATA) && 
							!second.getBody().getUserData().equals(Player.PLAYER_USER_DATA)) {
						second.getBody().setUserData(GameObject.DESTROY_BY_WALL_USER_DATA);
						return;
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
					/*Debug.e("TU");
					if (firstUD.equals(WALL_BOTTOM_USER_DATA) || firstUD.equals(WALL_TOP_USER_DATA) || 
							firstUD.equals(WALL_VERTICAL_USER_DATA) ||
							secondUD.equals(WALL_BOTTOM_USER_DATA) || secondUD.equals(WALL_TOP_USER_DATA) || 
							secondUD.equals(WALL_VERTICAL_USER_DATA)) {
						return;
					} else {
						contact.setEnabled(false);
						first.getBody().setUserData(GameObject.DESTROY_USER_DATA);
						second.getBody().setUserData(GameObject.DESTROY_USER_DATA);
					}*/
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
	
	private void respawnPlayer() {
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
	
	private void deleteObjectsForDestroy() {
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
	
	
	private Player player;
	private List<GameObject> gameObjects;

	private void loadLevel(int levelID) throws IOException {
		gameObjects = new ArrayList<GameObject>();
		
	    final LevelLoader levelLoader = new LevelLoader("");
	    
	    levelLoader.registerEntityLoader("level", new IEntityLoader() {
	        public IEntity onLoadEntity(final String pEntityName, final Attributes pAttributes) {
	            enemies = SAXUtils.getIntAttributeOrThrow(
	            		pAttributes, TAG_LEVEL_ATTRIBUTE_ENEMIES);
	            
	            // TODO later we will specify camera BOUNDS and create invisible walls
	            // on the beginning and on the end of the level.

	            return GameScene.this;
	        }
	    });
	    
	    levelLoader.registerEntityLoader(TAG_ENTITY, new IEntityLoader() {
	    	        public IEntity onLoadEntity(final String pEntityName, final Attributes pAttributes) {
	    	            final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
	    	            final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
	    	            final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	    	            
	    	            final GameObject levelObject;
	    	            
	    	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
	    	                player = new Player(x, y, vbom, camera, physicsWorld);
	    	                levelObject = player;
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_METEOR)) {
	    	            	levelObject = new Meteor(x, y, vbom, camera, physicsWorld);
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BULLET)) {
	    	            	levelObject = new Bullet(x, y, vbom, camera, physicsWorld);
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_LIGHT_FIGHTER)) {
	    	            	levelObject = new LightFighter(x, y, vbom, camera, physicsWorld, ActiveEnemyType.LEFT);
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_LIGHT_FIGHTER)) {
	    	            	levelObject = new LightFighter(x, y, vbom, camera, physicsWorld, ActiveEnemyType.RIGHT);
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_HEAVY_FIGHTER)) {
	    	            	levelObject = new HeavyFighter(x, y, vbom, camera, physicsWorld, ActiveEnemyType.LEFT);
	    	            } else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_HEAVY_FIGHTER)) {
	    	            	levelObject = new HeavyFighter(x, y, vbom, camera, physicsWorld, ActiveEnemyType.RIGHT);
	    	            } else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEFT_CRUISER)) {
	    	            	levelObject = new Cruiser(x, y, vbom, camera, physicsWorld, ActiveEnemyType.LEFT);
	    	            } else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RIGHT_CRUISER)) {
	    	            	levelObject = new Cruiser(x, y, vbom, camera, physicsWorld, ActiveEnemyType.RIGHT);
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
		GameObject missile = new Missile(x, y, vbom, camera, physicsWorld, type);
		missile.setCullingEnabled(true);
		attachChild(missile);
		gameObjects.add(missile);
	}
}
