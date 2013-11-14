package com.project.harbinger.scene;

import java.io.IOException;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.SAXUtils;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.level.IEntityLoader;
import org.andengine.util.level.LevelLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.xml.sax.Attributes;

import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.project.harbinger.gameObject.Meteor;
import com.project.harbinger.gameObject.Missile;
import com.project.harbinger.gameObject.Player;
import com.project.harbinger.manager.SceneManager;
import com.project.harbinger.manager.SceneManager.SceneType;

public class GameScene extends BaseScene {

	private HUD gameHUD;
	private Text scoreText;
	private PhysicsWorld physicsWorld;
	private Text debugPlayerCoordinates;
	
	@Override
	public void createScene() {
		createBackground();
		createHUD();
		createPhysics();
		loadLevel(0);
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
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
	
	private void createHUD() {
		gameHUD = new HUD();
		
		scoreText = new Text(20, 240, resourcesManager.getFont(),
				"Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText.setPosition(0, 0);
		scoreText.setText("Score: 0");
		gameHUD.attachChild(scoreText);
		
		debugPlayerCoordinates = new Text(10, 10, resourcesManager.getFont(),
				"x: 1234567890- y: 1234567890-", new TextOptions(HorizontalAlign.LEFT), vbom);
		debugPlayerCoordinates.setPosition(0, 700);
		debugPlayerCoordinates.setText("x: y:");
		debugPlayerCoordinates.setSize(300, 100);
		gameHUD.attachChild(debugPlayerCoordinates);
		
		final Rectangle left = new Rectangle(20, 200, 60, 60, vbom) {
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
		
	    final Rectangle right = new Rectangle(100, 200, 60, 60, vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            if (touchEvent.isActionDown()) {
	            	player.setVelocity(+10, 0);
	            	GameScene.this.creteMissile(100, 100);
	            } else if (touchEvent.isActionUp() || 
	            		touchEvent.getMotionEvent().getActionMasked() == MotionEvent.ACTION_MOVE) {
	            	player.setVelocity(0, 0);
	            }
	            return true;
	        };
	    };
	    
	    final Rectangle up = new Rectangle(100, 100, 60, 60, vbom) {
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
	    
	    final Rectangle down = new Rectangle(100, 300, 60, 60, vbom) {
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
	    final Rectangle fire = new Rectangle(300, 200, 60, 60, vbom) {
	        public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
	            creteMissile(player.getX() + 20, player.getY() - 35);
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
	
	public void debugSetPlayerCoordinates(float x, float y) {
		String xC = "x: " + String.valueOf(x);
		String yC = "y: " + String.valueOf(y);
		
		debugPlayerCoordinates.setText(xC + "\n" + yC);
	}
	
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false);
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(createContactListener());
	}
	
	private ContactListener createContactListener() {
		ContactListener contactListener = new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				Fixture first = contact.getFixtureA();
				Fixture second = contact.getFixtureB();
				if (first.getBody().getUserData().equals("player") && 
					second.getBody().getUserData().equals("meteor")) {
					Debug.e("Player z meteorem!");
				}
			}

			@Override
			public void endContact(Contact contact) {}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
			
		};
		
		return contactListener;		
	}
	
	// level loading
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	private static final String TAG_ENTITY = "entity";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_METEOR = "meteor";
	
	private Player player;

	private void loadLevel(int levelID) {
	    final LevelLoader levelLoader = new LevelLoader("");
	    
	    final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
	    
	    levelLoader.registerEntityLoader("level", new IEntityLoader() {
	        public IEntity onLoadEntity(final String pEntityName, final Attributes pAttributes) {
	            final int width = SAXUtils.getIntAttributeOrThrow(
	            		pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
	            final int height = SAXUtils.getIntAttributeOrThrow(
	            		pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
	            
	            // TODO later we will specify camera BOUNDS and create invisible walls
	            // on the beginning and on the end of the level.

	            return GameScene.this;
	        }
	    });
	    
	    levelLoader.registerEntityLoader("entity", new IEntityLoader() {
	    	        public IEntity onLoadEntity(final String pEntityName, final Attributes pAttributes) {
	    	            final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
	    	            final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
	    	            final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	    	            
	    	            final Sprite levelObject;
	    	            
	    	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
	    	                player = new Player(x, y, vbom, camera, physicsWorld);
	    	                levelObject = player;
	    	            } else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_METEOR)) {
	    	            	levelObject = new Meteor(x, y, vbom, camera, physicsWorld);
	    	            } else {
	    	            	levelObject = null;
	    	            }

	    	            levelObject.setCullingEnabled(true);

	    	            return levelObject;
	    	        }
	    	    });

	    	    try {
					levelLoader.loadLevelFromAsset(activity.getAssets(), "levels/" + levelID + ".lvl");
				} catch (IOException e) {
					Debug.e(e);
				}
	}
	
	public void creteMissile(float x, float y) {
		Sprite missile = new Missile(x, y, vbom, camera, physicsWorld);
		missile.setCullingEnabled(true);
		this.attachChild(missile);
	}
	
}
